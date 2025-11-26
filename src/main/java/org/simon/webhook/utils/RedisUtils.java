package org.simon.webhook.utils;


import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Service
public class RedisUtils {
    private RedisTemplate<String, Object> redisTemplate;
    /**
     * 默认过期时长为24小时，单位：秒
     */
    public final static long DEFAULT_EXPIRE = 60 * 60 * 24L * 7;
    /**
     * 超时时间7日
     */
    public final static long SEVEN_DAY_EXPIRE = DEFAULT_EXPIRE * 7L;
    /**
     * 过期时长为1小时，单位：秒
     */
    public final static long HOUR_ONE_EXPIRE = 60 * 60 * 1L;
    /**
     * 过期时长为6小时，单位：秒
     */
    public final static long HOUR_SIX_EXPIRE = 60 * 60 * 6L;
    /**
     * 不设置过期时长
     */
    public final static long NOT_EXPIRE = -1L;

    public void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(key, value);
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public boolean setIfAbsent(String key, Object value, long expire) {
        Boolean b = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (b != null && b) {
            if (expire != NOT_EXPIRE) {
                expire(key, expire);
            }
            return true;
        }
        return false;
    }

    public void set(String key, Object value) {
        set(key, value, NOT_EXPIRE);
    }

    public Object get(String key, long expire) {
        Object value = redisTemplate.opsForValue().get(key);
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
        return value;
    }

    public Object get(String key) {
        return get(key, NOT_EXPIRE);
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void deleteByPattern(String pattern) {
        redisTemplate.delete(keys(pattern));
    }

    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    public void delete(String... keys) {
        List<String> list = new ArrayList<>();
        for (String key : keys) {
            list.add(key);
        }
        redisTemplate.delete(list);
    }

    public Object hIncrement(String key, String field, double delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    public Object hIncrement(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }


    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Map<String, Object> hGetAll(String key) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(key);
    }

    public void hMSet(String key, Map<String, Object> map) {
        hMSet(key, map, DEFAULT_EXPIRE);
    }

    public void hMSet(String key, Map<String, Object> map, long expire) {
        redisTemplate.opsForHash().putAll(key, map);

        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public void hSet(String key, String field, Object value) {
        hSet(key, field, value, DEFAULT_EXPIRE);
    }

    public void hSet(String key, String field, Object value, long expire) {
        redisTemplate.opsForHash().put(key, field, value);

        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public void expire(String key, long expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }

    public void hDel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public void leftPush(String key, Object value) {
        leftPush(key, value, DEFAULT_EXPIRE);
    }

    public void leftPush(String key, Object value, long expire) {
        redisTemplate.opsForList().leftPush(key, value);

        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }


    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

//    public void setSet(String key,Object... values){
//        setSet(key,DEFAULT_EXPIRE,values);
//    }

    public void setSet(String key, long expire, Object... values) {
        redisTemplate.opsForSet().add(key, values);
        if (expire != NOT_EXPIRE) {
            expire(key, expire);
        }
    }

    public Set<Object> setGet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public boolean setIsExitFiled(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    /**
     * 添加自增方法
     */
    public long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            if (!redisTemplate.hasKey(key)) {
                set(key, delta, DEFAULT_EXPIRE);
            }
            return redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public long incr24H(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            if (!redisTemplate.hasKey(key)) {
                set(key, delta, 60 * 60 * 24L);
            }
            return redisTemplate.opsForValue().increment(key, delta);
        }
    }

    /**
     * @return boolean
     * @Author renBo
     * @Description 判断redis中是否存在对应key
     * @Date 2025年4月14日17:23:15
     * @Param [key, value]
     */
    public boolean isExist(final String key) {
        return redisTemplate.hasKey(key);
    }

    public Long getExpire(final String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 判断hash中是否存在指定字段
     */
    public boolean hExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * hash字段自增
     */
    public Long hIncr(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, delta);
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Set<String> scan(String pattern) {
        return redisTemplate.execute((RedisConnection connection) -> {
            Set<String> keys = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(5000).build()
            )) {
                while (cursor.hasNext()) {
                    keys.add(new String(cursor.next()));
                }
            } catch (Exception ignored) {}
            return keys;
        });
    }

}
