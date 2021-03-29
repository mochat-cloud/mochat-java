package com.mochat.mochat.dao.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author  idea
 * @Date 2020-12-10 
 */

@Data
@TableName("mc_migrations" )
public class MigrationsEntity {
	@TableId(type = IdType.AUTO)
	private Integer id;

	private String migration;

	private Integer batch;
}
