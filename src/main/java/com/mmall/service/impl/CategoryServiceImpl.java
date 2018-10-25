package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
@Transactional
public class CategoryServiceImpl implements ICategoryService {
    private static Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<String> addCategory(int parentId,String categoryName){
        if(StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        int rowCount = this.categoryMapper.insertSelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("商品类型添加成功");
        }
        return ServerResponse.createByErrorMessage("商品类型添加失败");
    }

    public ServerResponse<String> updateCategoryName(Integer categoryId,String categoryName){
        if(StringUtils.isBlank(categoryName) || categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = this.categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("修改商品类型成功");
        }
        return ServerResponse.createByErrorMessage("修改商品类型失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> childrenParallelCategory = this.categoryMapper.getChildrenParallelCategory(categoryId);
        if(CollectionUtils.isEmpty(childrenParallelCategory)){
            logger.info("没有子节点");
        }
        return ServerResponse.createBySuccess(childrenParallelCategory);
    }

    public ServerResponse<List<Integer>> getChildrenDeepCategory(Integer categoryId){
        HashSet<Integer> ids = Sets.newHashSet();
        getchildren(ids,categoryId);
        ArrayList<Integer> lists = Lists.newArrayList();
        for(Integer id : ids){
            lists.add(id);
        }
        return ServerResponse.createBySuccess(lists);
    }

    private Set<Integer> getchildren(Set<Integer> set,Integer categoryId){
        set.add(categoryId);
        List<Integer> childrenParallelId = this.categoryMapper.getChildrenParallelId(categoryId);
        if(!CollectionUtils.isEmpty(childrenParallelId)){
            for(Integer id : childrenParallelId){
                getchildren(set,id);
            }
        }
        return set;
    }
}
