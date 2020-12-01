package pers.cocoadel.learning.mysql.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.cocoadel.learning.mysql.domain.Mytbl;
import pers.cocoadel.learning.mysql.reposity.MytblReposity;

import java.util.LinkedList;
import java.util.List;

@Service
public class MytblService {

    private final MytblReposity mytblReposity;

    @Autowired
    public MytblService(MytblReposity mytblReposity) {
        this.mytblReposity = mytblReposity;
    }

    @Transactional
    public void insert(){
        List<Mytbl> mytbls = createData(1,5);
        mytblReposity.saveAll(mytbls);
    }

    private List<Mytbl> createData(int start,int count){
        List<Mytbl> list = new LinkedList<>();
        for(int i = 0; i < count;i++){
            Mytbl mytbl = new Mytbl();
            mytbl.setId(i + start);
            mytbl.setName("master-"+i);
            list.add(mytbl);
        }
        return list;
    }

    @Transactional
    public void show(){
        List<Mytbl> list = mytblReposity.findAll();
        for (Mytbl mytbl : list) {
            System.out.println(mytbl);
        }
    }

    @Transactional
    public void insertAndShow(){
        List<Mytbl> mytbls = createData(6,5);
        mytblReposity.saveAll(mytbls);
        mytblReposity.flush();
        List<Mytbl> list = mytblReposity.findAll();
        for (Mytbl mytbl : list) {
            System.out.println(mytbl);
        }
    }


}
