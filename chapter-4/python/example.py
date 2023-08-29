import redis

client = redis.Redis(host='redis-16887.c1.us-east1-2.gce.cloud.redislabs.com', port=16887, username='default', password='redis-stack-2023')
client.set("Redis", "Stack")
print(client.get("Redis"))

departmentList = ['Sales','Solution Architects','Technical Enablement Managers']
client.lpush("redis:departments", *departmentList)
print(client.lrange("redis:departments", 0, -1))
print(client.rpop("redis:departments"))
print(client.lrange("redis:departments", 0, 10))

favouriteCitySet = {'Macerata','Roma','Atlantis'}
print(favouriteCitySet)
client.sadd("city:favourites", *favouriteCitySet)
print(client.smembers("city:favourites"))

cityPopulation = {"city:italy:roma": 2748109, "city:italy:macerata": 40820, "city:italy:catania": 298762}
print(cityPopulation)
print(client.zadd("city:italy", cityPopulation))
res = client.zrange("city:italy", 0, -1, withscores=True)
print(res)
cnt = client.zcard("city:italy")
print(cnt)


employee = {"firstname":"Luigi",
    "lastname":"Fugaro",
    "company":"Redis",
    "department":"Solutions Architect",
    "note":"No matter what, we are all in sales!"
}
print(employee)
client.hset("employee:1", mapping=employee)
print(client.hgetall("employee:1"))
