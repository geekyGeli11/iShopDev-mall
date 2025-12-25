package com.macro.mall.service;

import com.macro.mall.dto.PmsProductDiyConfigVO;
import com.macro.mall.dto.PmsProductExportDTO;
import com.macro.mall.dto.PmsProductListVO;
import com.macro.mall.dto.PmsProductParam;
import com.macro.mall.dto.PmsProductQueryParam;
import com.macro.mall.dto.PmsProductResult;
import com.macro.mall.dto.PmsProductShareResult;
import com.macro.mall.model.PmsProduct;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品管理Service
 * Created by macro on 2018/4/26.
 */
public interface PmsProductService {
    /**
     * 创建商品
     */
    @Transactional(isolation = Isolation.DEFAULT,propagation = Propagation.REQUIRED)
    int create(PmsProductParam productParam);

    /**
     * 根据商品编号获取更新信息
     */
    PmsProductResult getUpdateInfo(Long id);

    /**
     * 更新商品
     */
    @Transactional
    int update(Long id, PmsProductParam productParam);

    /**
     * 分页查询商品
     */
    List<PmsProduct> list(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量修改审核状态
     * @param ids 产品id
     * @param verifyStatus 审核状态
     * @param detail 审核详情
     */
    @Transactional
    int updateVerifyStatus(List<Long> ids, Integer verifyStatus, String detail);

    /**
     * 批量修改商品上架状态
     */
    int updatePublishStatus(List<Long> ids, Integer publishStatus);

    /**
     * 批量修改商品推荐状态
     */
    int updateRecommendStatus(List<Long> ids, Integer recommendStatus);

    /**
     * 批量修改新品状态
     */
    int updateNewStatus(List<Long> ids, Integer newStatus);

    /**
     * 批量删除商品
     */
    int updateDeleteStatus(List<Long> ids, Integer deleteStatus);

    /**
     * 根据商品名称或者货号模糊查询
     */
    List<PmsProduct> list(String keyword);

    /**
     * 获取商品DIY配置
     */
    PmsProduct getDiyConfig(Long id);

    /**
     * 获取商品DIY配置详情（包含模板信息）
     */
    PmsProductDiyConfigVO getDiyConfigDetail(Long id);

    /**
     * 更新商品DIY配置
     */
    int updateDiyConfig(Long id, Byte diyEnabled, Long diyTemplateId);

    /**
     * 批量修改商品DIY状态
     */
    int updateDiyStatus(List<Long> ids, Byte diyEnabled);

    /**
     * 分页查询商品列表（包含运费模板和学校信息）
     */
    List<PmsProductListVO> listWithDetails(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);

    /**
     * 统计商品数量（包含学校筛选）
     * @param productQueryParam 查询参数
     * @return 商品数量
     */
    long countProductsWithDetails(PmsProductQueryParam productQueryParam);

    /**
     * 批量修改商品学校关联
     * @param productIds 商品ID列表
     * @param schoolIds 学校ID列表
     */
    @Transactional
    int updateProductSchools(List<Long> productIds, List<Long> schoolIds);

    /**
     * 批量修改商品分类
     * @param productIds 商品ID列表
     * @param productCategoryId 分类ID
     * @return 修改成功的数量
     */
    int updateProductCategory(List<Long> productIds, Long productCategoryId);

    /**
     * 导出商品数据
     * @param productQueryParam 查询参数
     * @return 导出数据列表
     */
    List<PmsProductExportDTO> exportProducts(PmsProductQueryParam productQueryParam);

    /**
     * 统计商品数量
     * @param productQueryParam 查询参数
     * @return 商品数量
     */
    long countProducts(PmsProductQueryParam productQueryParam);

    /**
     * 分页导出商品数据
     * @param productQueryParam 查询参数
     * @param pageSize 页大小
     * @param pageNum 页码
     * @return 导出数据列表
     */
    List<PmsProductExportDTO> exportProductsPaged(PmsProductQueryParam productQueryParam, int pageSize, int pageNum);

    /**
     * 生成商品分享信息
     * @param productId 商品ID
     * @return 分享信息
     */
    PmsProductShareResult generateShareInfo(Long productId);

    /**
     * 分页查询商品（包含门店SKU库存信息）
     * @param productQueryParam 查询参数
     * @param pageSize 页大小
     * @param pageNum 页码
     * @return 商品列表（包含门店库存）
     */
    List<com.macro.mall.dto.PmsProductWithStoreStockVO> listWithStoreStock(PmsProductQueryParam productQueryParam, Integer pageSize, Integer pageNum);
}
