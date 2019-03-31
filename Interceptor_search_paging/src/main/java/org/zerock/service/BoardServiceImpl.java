package org.zerock.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.domain.BoardVO;
import org.zerock.domain.Criteria;
import org.zerock.domain.SearchCriteria;
import org.zerock.persistence.BoardDAO;

@Service
public class BoardServiceImpl implements BoardService {

	@Inject
	private BoardDAO dao;

	@Transactional
	@Override
	public void regist(BoardVO board) throws Exception {
		dao.create(board);
		
		String[] files = board.getFiles(); // �߰��� ���� �迭���� �����ͼ�.
		
		if(files == null) { return; } // �߰��� ������ ������ ��.
		
		for (String fileName : files) { // �߰��� ���ϵ��� �ϳ��� DB�� ����.
			dao.addAttach(fileName);
		}
		
	}

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@Override
	public BoardVO read(Integer bno) throws Exception {
		dao.updateViewCnt(bno);
		return dao.read(bno);
	}

	@Transactional
	@Override
	public void modify(BoardVO board) throws Exception {
		dao.update(board);
		
		Integer bno = board.getBno(); // jsp���Ͽ��� form �±׿� �Ǿ� ���� ���� boardVO ��ü�� �޾Ҵ�. ���� ��ü���� bno ���� ������.
		dao.deleteAllAttach(bno); // bno ���� �ش��ϴ� ÷�����ϵ��� ���� �����Ѵ�.
		
		String[] files = board.getFiles(); // �����̸� ��Ϲ迭�� �����´�.
		
		if(files == null) { return; } // ���� ÷�������� ���ٸ� ȣ�� �ߴ� ������ ���ư���.
		
		for (String fileName : files) { // ���ϵ��� �ϳ��� ������ �ݺ������� ������.
			dao.replaceAttach(fileName, bno); // bno �� �ش�Ǵ� ÷��
		}
		
	}

	@Transactional
	@Override
	public void remove(Integer bno) throws Exception {
		dao.deleteAllAttach(bno); // �����۾��� ��쿣 ÷�������� tbl_board�� bno�� �����ϱ� ������ ÷�������� ���� �����ϰ� 
		dao.delete(bno); // �� ������ �Խù��� �������ش�.
	}

	@Override
	public List<BoardVO> listAll() throws Exception {
		return dao.listAll();
	}

	@Override
	public List<BoardVO> listCriteria(Criteria cri) throws Exception {

		return dao.listCriteria(cri);
	}

	@Override
	public int listCountCriteria(Criteria cri) throws Exception {

		return dao.countPaging(cri);
	}

	@Override
	public List<BoardVO> listSearchCriteria(SearchCriteria cri) throws Exception {

		return dao.listSearch(cri);
	}

	@Override
	public int listSearchCount(SearchCriteria cri) throws Exception {

		return dao.listSearchCount(cri);
	}

	@Override
	public List<String> getAttach(Integer bno) throws Exception {
		return dao.getAttach(bno);
	}


}
