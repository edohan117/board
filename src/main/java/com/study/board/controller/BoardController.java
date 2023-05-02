package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private BoardService boardService;

    @GetMapping("/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword) {
        log.info("board List 로들어왔어요");
        Page<Board> list = null;

        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword , pageable);
        }


        int nowPage = list.getPageable().getPageNumber() + 1;

        int startPage = Math.max(nowPage - 4 , 1);

        int endPage = Math.min(nowPage + 5 , list.getTotalPages());

        model.addAttribute("list", list );
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);
        return "board/boardList";
    }

    @GetMapping("/write")
    public String boardWriteForm(){

        return "board/boardWrite";
    }

    @PostMapping("/writePro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {

        boardService.write(board, file);

        model.addAttribute("message", "글작성이 완료 되었습니다");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/view")
    public String boardView(Model model, Integer id) {

        model.addAttribute("board", boardService.boardView(id));

        return "board/boardView";
    }

    @GetMapping("/delete")
    public String boardDelete(Integer id) {

        boardService.boardDelete(id);
        return "redirect:/board/list";
    }

    @GetMapping("/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,
                              Model model) {
        model.addAttribute("board", boardService.boardView(id));
        return "board/boardModify";
    }

    @PostMapping("/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id,
                              Board board, MultipartFile file) throws Exception{
        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setTitle(board.getContent());

        boardService.write(boardTemp, file);

        return "redirect:/board/list";
    }

}
