package pers.cocoade.learning.mysql;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法-产生Long类型ID
 */
@Component
public class MagicSnowFlake
{
    //其实时间戳   2017-01-01 00:00:00
    private final static long twepoch = 1483200000000L;

    //10bit（位）的工作机器id  中IP标识所占的位数 8bit（位）
    private final static long ipIdBits = 8L;

    //IP标识最大值 255  即2的8次方减一。
    private final static long ipIdMax = ~(-1L << ipIdBits);

    //10bit（位）的工作机器id  中数字标识id所占的位数 2bit（位）
    private final static long dataCenterIdBits = 2L;

    //数字标识id最大值 3  即2的2次方减一。
    private final static long dataCenterIdMax = ~(-1L << dataCenterIdBits);

    //序列在id中占的位数 12bit
    private final static long seqBits = 12L;

    //序列最大值 4095 即2的12次方减一。
    private final static long seqMax = ~(-1L << seqBits);

    // 64位的数字：首位0  随后41位表示时间戳 随后10位工作机器id（8位IP标识 + 2位数字标识） 最后12位序列号
    private final static long dataCenterIdLeftShift = seqBits;
    private final static long ipIdLeftShift = seqBits + dataCenterIdBits;
    private final static long timeLeftShift = seqBits + dataCenterIdBits + ipIdBits;

    //IP标识(0~255)
    @Value("${snowFlake.serverId:0}")
    private long ipId = 0;

    // 数据中心ID(0~3)
    @Value("${snowFlake.centreId:0}")
    private long dataCenterId = 0;

    // 毫秒内序列(0~4095)
    private long seq = 0L;

    // 上次生成ID的时间截
    private long lastTime = -1L;

    public MagicSnowFlake()
    {
        
    }

    public synchronized long nextId()
    {
        long nowTime = System.currentTimeMillis();

        if (nowTime < lastTime)
        {
            throw new IllegalArgumentException("dataCenterId不在正常范围内(0~" + dataCenterIdMax + ")" + dataCenterId);
        }

        if (nowTime == lastTime)
        {
            seq = (seq + 1) & seqMax;
            if (seq == 0)
            {
                nowTime = getNextTimeStamp();
            }
        }
        else
        {
            seq = 0L;
        }

        lastTime = nowTime;

        return ((nowTime - twepoch) << timeLeftShift)
                | (ipId << ipIdLeftShift)
                | (dataCenterId << dataCenterIdLeftShift)
                | seq;
    }

    private long getNextTimeStamp()
    {
        long nowTime;
        do
        {
            nowTime = System.currentTimeMillis();
        }
        while (nowTime <= lastTime);
        return nowTime;
    }
}
