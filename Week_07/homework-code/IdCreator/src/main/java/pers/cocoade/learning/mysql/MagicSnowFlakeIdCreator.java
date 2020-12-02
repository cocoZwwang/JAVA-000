package pers.cocoade.learning.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("MagicSnowFlake")
public class MagicSnowFlakeIdCreator implements IdCreator<Long> {

    private final MagicSnowFlake magicSnowFlake;

    @Autowired
    public MagicSnowFlakeIdCreator(MagicSnowFlake magicSnowFlake) {
        this.magicSnowFlake = magicSnowFlake;
    }

    @Override
    public Long nextId() {
        return magicSnowFlake.nextId();
    }
}
