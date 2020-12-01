package pers.cocoadel.learing.mysql.dao;

import pers.cocoadel.learing.mysql.domain.Mytbl;

import java.util.List;

public interface MytblDao {

    void batchSave(List<Mytbl> list);

    List<Mytbl>  findAll();
}
