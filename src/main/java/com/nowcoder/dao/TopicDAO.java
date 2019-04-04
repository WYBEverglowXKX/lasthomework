package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.Topic;
import org.apache.ibatis.annotations.*;

import java.util.List;


/**
 * 问题DAO
 */
@Mapper
public interface TopicDAO {
    String TABLE_NAME = " topic ";
    String INSERT_FIELDS = " topic_name, topic_img";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{topicName},#{topicImg}"})
    int addTopic(Question question);

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME})
    List<Topic> getTopic();

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where id = #{topicId}"})
    Topic getTopicById(int topicId);

}
