package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 问题DAO
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, topic, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{topic},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where id=#{id}"})
    Question getById(int id);

    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    @Select({"<script>",
                "select",
                " id, title, content, topic, created_date, user_id, comment_count ",
                " from question ",
                " where topic in",
                "<foreach collection='topicList' item='topic' open='(' separator=',' close=')'>",
                "#{topic}",
                "</foreach>",
            "</script>"})
    List<Question> getByTopic(@Param("topicList") List<Integer> topicList);

    @Select({"select " + SELECT_FIELDS + " from " + TABLE_NAME + " where topic=#{topicId}"})
    List<Question> getBySoleTopic(int topicId);
}
