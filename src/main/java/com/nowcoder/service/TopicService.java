package com.nowcoder.service;

import com.nowcoder.dao.TopicDAO;
import com.nowcoder.model.Topic;
import com.nowcoder.util.JedisAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by wang on 2019/4/2.
 */
@Service
public class TopicService {
    @Autowired
    TopicDAO topicDAO;

    @Autowired
    JedisAdapter jedisAdapter;

    public List<Topic> getTopic() {
        return topicDAO.getTopic();
    }

    public Topic getTopicById(int topicId) {
        return topicDAO.getTopicById(topicId);
    }

    public void followTopic(int userId, int topicId) {
        Jedis jedis = jedisAdapter.getJedis();
        jedis.lpush(userId + "lalala", topicId + "");
    }

    public void unfollowTopic(int userId, int topicId) {
        Jedis jedis = jedisAdapter.getJedis();
        jedis.lrem(userId + "lalala", 0, topicId + "");
    }

    public List<String> getFollowTopic(int userId) {
        Jedis jedis = jedisAdapter.getJedis();
        List<String> followList = jedis.lrange("usreId", 0, -1);
        return followList;
    }



}
