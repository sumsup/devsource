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
		
		String[] files = board.getFiles(); // 추가된 파일 배열들을 가져와서.
		
		if(files == null) { return; } // 추가된 파일이 없으면 끝.
		
		for (String fileName : files) { // 추가된 파일들을 하나씩 DB에 저장.
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
		
		Integer bno = board.getBno(); // jsp파일에서 form 태그에 실어 보낸 것을 boardVO 객체로 받았다. 받은 객체에서 bno 값을 꺼낸다.
		dao.deleteAllAttach(bno); // bno 값에 해당하는 첨부파일들을 전부 삭제한다.
		
		String[] files = board.getFiles(); // 파일이름 목록배열을 가져온다.
		
		if(files == null) { return; } // 만약 첨부파일이 없다면 호출 했던 곳으로 돌아간다.
		
		for (String fileName : files) { // 파일들은 하나씩 꺼내서 반복문으로 돌린다.
			dao.replaceAttach(fileName, bno); // bno 에 해당되는 첨부
		}
		
	}

	@Transactional
	@Override
	public void remove(Integer bno) throws Exception {
		dao.deleteAllAttach(bno); // 삭제작업의 경우엔 첨부파일이 tbl_board의 bno를 참조하기 때문에 첨부파일을 먼저 삭제하고 
		dao.delete(bno); // 그 다음에 게시물을 삭제해준다.
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
