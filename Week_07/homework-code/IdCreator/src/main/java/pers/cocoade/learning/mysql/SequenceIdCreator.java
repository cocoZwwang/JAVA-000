package pers.cocoade.learning.mysql;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("SequenceId")
public class SequenceIdCreator implements IdCreator<Long>{
    private long currentValue;

    private long increment;

    private long add = 0;

    private final JdbcTemplate jdbcTemplate;

    private final static String SELECT_SQL = String.format("select my_seq_nextval('%s')","test_seq");

    @Autowired
    public SequenceIdCreator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public synchronized Long nextId() {
        if(add == increment){
            applyId();
        }
        return currentValue + (add++);
    }

    private void applyId() throws RuntimeException{
        try {
            String s = jdbcTemplate.queryForObject(SELECT_SQL, String.class);
            assert s != null;
            String[] ss = s.split(",");
            currentValue = Long.parseLong(ss[0]);
            increment = Long.parseLong(ss[1]);
            add = 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
