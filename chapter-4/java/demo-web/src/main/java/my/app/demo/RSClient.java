package my.app.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

public class RSClient {


        private static Logger log = LoggerFactory.getLogger(RSClient.class);

        private static RSClient instance = null;

        private static JedisPool jedisPool;

        public static RSClient getInstance(String ip, final int port, String username, String password) {
            if (instance == null) {
                synchronized(RSClient.class) {
                    if (instance == null) {
                        instance = new RSClient(ip, port, username, password);
                    }
                }
            }
            return instance;
        }

        private RSClient(String ip, int port, String username, String password) {
            if (jedisPool == null) {
                jedisPool = new JedisPool(ip, port, username, password);
            }
        }

    public String set(final String key, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.set(key, value);
        } catch (Exception ex) {
            log.error("Exception caught in set", ex);
        }
        return null;
    }

    public String get(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception ex) {
            log.error("Exception caught in get", ex);
        }
        return null;
    }
    public Long lpush(final String key, final String... strings) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, strings);
        } catch (Exception ex) {
            log.error("Exception caught in lpush", ex);
        }
        return null;
    }

    public String rpop(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.rpop(key);
        } catch (Exception ex) {
            log.error("Exception caught in rpop", ex);
        }
        return null;
    }

    public Long sadd(final String key, final String... members) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, members);
        } catch (Exception ex) {
            log.error("Exception caught in sadd", ex);
        }
        return null;
    }

    public Set<String> smembers(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.smembers(key);
        } catch (Exception ex) {
            log.error("Exception caught in smembers", ex);
        }
        return new HashSet<String>();
    }

    public boolean sismember(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, member);
        } catch (Exception ex) {
            log.error("Exception caught in sismember", ex);
        }
        return Boolean.FALSE;
    }

    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zadd(key, scoreMembers);
        } catch (Exception ex) {
            log.error("Exception caught in zadd", ex);
        }
        return 0L;
    }

    public List<String> zrange(final String key, final long start, final long stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrange(key, start, stop);
        } catch (Exception ex) {
            log.error("Exception caught in zrange", ex);
        }
        return new ArrayList<>();
    }

    public List<String> zrangeByScore(final String key, final double start, final double stop) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zrangeByScore(key, start, stop);
        } catch (Exception ex) {
            log.error("Exception caught in zrangeByScore", ex);
        }
        return new ArrayList<>();
    }

    public Double zscore(final String key, final String member) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception ex) {
            log.error("Exception caught in zscore", ex);
        }
        return -1.0;
    }

    public Long hset(final String key, final String field, final String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, field, value);
        } catch (Exception ex) {
            log.error("Exception caught in hset", ex);
        }
        return -1l;
    }

    public Long hset(final String key, final Map<String,String> fieldValuePairs) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hset(key, fieldValuePairs);
        } catch (Exception ex) {
            log.error("Exception caught in hset for map", ex);
        }
        return -1l;
    }

    public String hget(final String key, final String field) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hget(key, field);
        } catch (Exception ex) {
            log.error("Exception caught in hget", ex);
        }
        return "";
    }

    public Map<String,String> hgetAll(final String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception ex) {
            log.error("Exception caught in hgetAll", ex);
        }
        return new HashMap<>();
    }
    public void flushAll() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
        } catch (Exception ex) {
            log.error("Exception caught in flushAll", ex);
        }
    }

    public void destroyInstance() {
        jedisPool = null;
        instance = null;
    }

}
