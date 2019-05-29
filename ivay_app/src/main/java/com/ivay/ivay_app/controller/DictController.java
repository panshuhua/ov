package com.ivay.ivay_app.controller;

import com.ivay.ivay_app.dao.DictDao;
import com.ivay.ivay_app.model.Dict;
import com.ivay.ivay_common.table.PageTableHandler;
import com.ivay.ivay_common.table.PageTableHandler.CountHandler;
import com.ivay.ivay_common.table.PageTableHandler.ListHandler;
import com.ivay.ivay_common.table.PageTableRequest;
import com.ivay.ivay_common.table.PageTableResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dicts")
public class DictController {

	@Autowired
	private DictDao dictDao;

	@PreAuthorize("hasAuthority('dict:add')")
	@PostMapping
	@ApiOperation(value = "保存")
	public Dict save(@RequestBody Dict dict) {
		Dict d = dictDao.getByTypeAndK(dict.getType(), dict.getK());
		if (d != null) {
			throw new IllegalArgumentException("类型和key已存在");
		}
		dictDao.save(dict);

		return dict;
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取")
	public Dict get(@PathVariable Long id) {
		return dictDao.getById(id);
	}

	@PreAuthorize("hasAuthority('dict:add')")
	@PutMapping
	@ApiOperation(value = "修改")
	public Dict update(@RequestBody Dict dict) {
		dictDao.update(dict);

		return dict;
	}

	@PreAuthorize("hasAuthority('dict:query')")
	@GetMapping(params = { "start", "length" })
	@ApiOperation(value = "列表")
	public PageTableResponse list(PageTableRequest request) {
		return new PageTableHandler(new CountHandler() {

			@Override
			public int count(PageTableRequest request) {
				return dictDao.count(request.getParams());
			}
		}, new ListHandler() {

			@Override
			public List<Dict> list(PageTableRequest request) {
				return dictDao.list(request.getParams(), request.getOffset(), request.getLimit());
			}
		}).handle(request);
	}

	@PreAuthorize("hasAuthority('dict:del')")
	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除")
	public void delete(@PathVariable Long id) {
		dictDao.delete(id);
	}

	@GetMapping(params = "type")
	public List<Dict> listByType(String type) {
		return dictDao.listByType(type);
	}
}
