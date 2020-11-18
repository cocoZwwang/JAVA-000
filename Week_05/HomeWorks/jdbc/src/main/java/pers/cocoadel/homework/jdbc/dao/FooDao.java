package pers.cocoadel.homework.jdbc.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import pers.cocoadel.homework.jdbc.domain.Foo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FooDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SimpleJdbcInsert simpleJdbcInsert;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void insertData() {
        jdbcTemplate.update("insert into FOO(BAR) values (?)","b");
        jdbcTemplate.update("insert into FOO(BAR) values (?)","c");

        Map<String, Object> map = new HashMap<>();
        map.put("BAR","d");
        simpleJdbcInsert.executeAndReturnKey(map);
    }

    public void showData() {
        List<Foo> foos = jdbcTemplate.query("select * from foo", (resultSet, i) -> Foo.builder()
                .id(resultSet.getLong(1))
                .bar(resultSet.getString(2))
                .build());
        foos.forEach(System.out::println);
    }

    public void batchInsetData(){
        List<String> bars = Arrays.asList("x","y","z");
        jdbcTemplate.batchUpdate("insert into foo(bar) values (?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, bars.get(i));
            }

            @Override
            public int getBatchSize() {
                return bars.size();
            }
        });

        List<Foo> foos = new ArrayList<>();
        foos.add(Foo.builder().id(1000L).bar("e").build());
        foos.add(Foo.builder().id(1001L).bar("f").build());
        namedParameterJdbcTemplate.batchUpdate("insert into foo(id,bar) values (:id,:bar)",
                SqlParameterSourceUtils.createBatch(foos));

    }
}
