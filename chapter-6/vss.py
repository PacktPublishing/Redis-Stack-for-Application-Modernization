from sentence_transformers import SentenceTransformer
from redis.commands.search.query import Query
from redis.commands.search.field import TextField, TagField, VectorField
from redis.commands.search.indexDefinition import IndexDefinition
import numpy as np
import redis


r = redis.Redis(host='127.0.0.1', port=6379, decode_responses=True)

# Reading the list of indexes for eventual creation
indexes = r.execute_command("FT._LIST")

if "doc_idx" not in indexes:
    # FT.CREATE doc_idx ON HASH PREFIX 1 doc: SCHEMA content AS content TEXT genre AS genre TAG embedding VECTOR HNSW 6 TYPE FLOAT32 DIM 384 DISTANCE_METRIC COSINE
    print("The index doc_idx does not exist, creating it")
    index_def = IndexDefinition(prefix=["doc:"])
    schema = (TextField("content"),
              TagField("genre"),
              VectorField("embedding", "HNSW", {"TYPE": "FLOAT32", "DIM": 384, "DISTANCE_METRIC": "COSINE"}))
    r.ft('doc_idx').create_index(schema, definition=index_def)


model = SentenceTransformer('sentence-transformers/multi-qa-MiniLM-L6-cos-v1')


text = "This is a technical document, it describes the SID sound chip of the Commodore 64"
r.hset('doc:1', mapping = {'embedding': model.encode(text).astype(np.float32).tobytes(),
                           'genre': 'technical',
                           'content': text})

text = "The Little Prince is a short story by Antoine de Saint-Exupéry, the best known of his literary production, published on April 6, 1943 in New York"
r.hset('doc:2', mapping = {'embedding': model.encode(text).astype(np.float32).tobytes(),
                           'genre': 'fantasy',
                       	   'content': text})

text = "Pasta alla carbonara is a characteristic dish of Lazio and more particularly of Rome, prepared with popular ingredients and with an intense flavour."
r.hset('doc:3', mapping = {'embedding': model.encode(text).astype(np.float32).tobytes(),
                           'genre': 'recipe',
                           'content': text})

text = "This post is about 8 bits computers, such as Commodore 64, ZX Spectrum and other home computers"
other_text = "The Adventures of Pinocchio is a fantasy novel for children written by Carlo Collodi, pseudonym of the journalist and writer Carlo Lorenzini, published for the first time in Florence in February 1883"

# Standard search
print()
print("***** Standard search *****")
q = Query("*=>[KNN 3 @embedding $vec AS score]").return_field("score").dialect(2)
res = r.ft("doc_idx").search(q, query_params={"vec": model.encode(other_text).astype(np.float32).tobytes()})
print(res)
print()

# Hybrid search
print("***** Hybrid search *****")
q = Query("(@genre:{technical})=>[KNN 2 @embedding $vec AS score]").return_field("score").dialect(2)
res = r.ft("doc_idx").search(q, query_params={"vec": model.encode(text).astype(np.float32).tobytes()})
print(res)
print()

# Range search, get a non decoded connection too
print("***** Range search *****")
r = redis.Redis(host='127.0.0.1', port=6379, decode_responses=False)

q = Query("@embedding:[VECTOR_RANGE $radius $vec]=>{$YIELD_DISTANCE_AS: score}") \
    .sort_by("score") \
    .return_field("score") \
    .dialect(2)

query_params = {
    "radius": 0.8,
    "vec": model.encode(other_text).astype(np.float32).tobytes()
}
res = r.ft("doc_idx").search(q, query_params)
print(res)
print()

# Profiling a query
#res = r.ft("doc_idx").profile(q, query_params={"vec": model.encode(text).astype(np.float32).tobytes()})
#print(res)


