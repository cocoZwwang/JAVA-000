package pers.cocoadel.learing.mysql.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pers.cocoadel.learing.mysql.domain.Mytbl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@Repository
public class MytblDaoImpl implements MytblDao {


    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = "insert into mytbl (id,name) values (?,?)";

    private static final String SELECT_SQL = "select * from mytbl";

    @Autowired
    public MytblDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void batchSave(List<Mytbl> list) {

        jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Mytbl mytbl = list.get(i);
                ps.setInt(1, mytbl.getId());
                ps.setString(2,mytbl.getName());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    @Override
    public List<Mytbl> findAll() {
        return jdbcTemplate.query(SELECT_SQL, (resultSet,i) -> {
            Mytbl mytbl = new Mytbl();
            mytbl.setId(resultSet.getInt(1));
            mytbl.setName(resultSet.getString(2));
            return mytbl;
        });
    }
}
