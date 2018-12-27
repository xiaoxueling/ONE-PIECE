package cn.main.admin.home.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminHomeService {

	@Autowired
	SqlSession sqlSession;

	public List<Map<String, Object>>getUserList(){
		return sqlSession.selectList("adminHomeDao.selectUserList");
	}
}
