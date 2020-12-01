package pers.cocoadel.learning.mysql.reposity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pers.cocoadel.learning.mysql.domain.Mytbl;

@Repository
public interface MytblReposity extends JpaRepository<Mytbl,Integer> {

}
