package pers.cocoade.learning.dubbo.b.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import pers.cocoade.learning.dubbo.api.domain.TransactionBill;

import java.util.Date;

@Mapper
public interface TransactionBillMapper {

    @Insert("insert into transaction_bill (id,buy_account,buy_amount,buy_currency," +
            "seller_account,seller_amount,seller_currency,state,create_time,update_time) " +
            "values (#{id},#{buyAccount},#{buyAmount},#{buyCurrency},#{sellerAccount},#{sellerAmount}," +
            "#{sellerCurrency},#{state},#{createTime},#{updateTime})")
    int add(TransactionBill bill);

    @Update("update transaction_bill set state = #{state},update_time = now() where id = #{id}")
    int updateSate(@Param("id") Long id, @Param("state") Integer state);

    @Delete("delete from transaction_bill where id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("select * from transaction_bill where id = #{id}")
    @Results(id = "transactionBillMap",value = {
            @Result(column = "id",property = "id",id = true,javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "buy_account",property = "buyAccount",javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "buy_amount",property = "buyAmount",javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "buy_currency",property = "buyCurrency",javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(column = "seller_account",property = "sellerAccount",javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "seller_amount",property = "sellerAmount",javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "seller_currency",property = "sellerCurrency",javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(column = "state",property = "state",javaType = Integer.class,jdbcType = JdbcType.SMALLINT),
            @Result(column = "create_time",property = "createTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "update_time",property = "updateTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP)
    })
    TransactionBill selectOne(@Param("id") Long id);
}
