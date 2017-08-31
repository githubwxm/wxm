package com.all580.order.entity;

import java.io.Serializable;

public class PackageOrderChain implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_package_order_chain.id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    private Integer id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_package_order_chain.order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    private Integer order_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_package_order_chain.ref_order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    private Integer ref_order_id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_package_order_chain
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_package_order_chain.id
     *
     * @return the value of t_package_order_chain.id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_package_order_chain.id
     *
     * @param id the value for t_package_order_chain.id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_package_order_chain.order_id
     *
     * @return the value of t_package_order_chain.order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public Integer getOrder_id() {
        return order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_package_order_chain.order_id
     *
     * @param order_id the value for t_package_order_chain.order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_package_order_chain.ref_order_id
     *
     * @return the value of t_package_order_chain.ref_order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public Integer getRef_order_id() {
        return ref_order_id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_package_order_chain.ref_order_id
     *
     * @param ref_order_id the value for t_package_order_chain.ref_order_id
     *
     * @mbggenerated Thu Aug 17 09:41:03 CST 2017
     */
    public void setRef_order_id(Integer ref_order_id) {
        this.ref_order_id = ref_order_id;
    }
}