<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
<script type="text/javascript">

	$(function(){
		//日历框架
		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "top-left"
		});

		//页面加载完毕后触发一个方法
		//默认展开列表的第一页，每页展现两条记录
		pageList(1,2);

		//为查询按钮绑定事件，触发pageList方法
		$("#searchBtn").click(function (){
			$("#hidden-name").val($.trim($("#search-name").val()))
			$("#hidden-owner").val($.trim($("#search-owner").val()))
			$("#hidden-conName").val($.trim($("#search-conName").val()))
			$("#hidden-cusName").val($.trim($("#search-cusName").val()))
			pageList(1,2);
		})

		//为全选复选框绑定事件，实现全选效果
		$("#qx").click(function (){
			$("input[name=xz]").prop("checked",this.checked);
		})

		//为选择复选框绑定事件，控制全选复选框
		$("#tranBody").on("click",$("input[name=xz]"),function (){
			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)
		})

		//执行删除操作
		$("#deleteBtn").click(function (){
			var $xz = $("input[name=xz]:checked");

			if ($xz.length==0){
				alert("请选择要删除的记录")
			}else {
				if (confirm("确定删除所选中的信息吗？")){
					var param = ""

					for (var i = 0; i < $xz.length; i++){
						param += "id="+$($xz[i]).val();

						if (i < $xz.length-1){
							param += "&";
						}
					}

					$.ajax({
						url: "workbench/tran/delete.do",
						data: param,
						type: "post",
						dataType: "json",
						success: function (data) {
							if (data.success) {
								//局部刷新市场活动信息列表
								//pageList(1, 2)

								pageList($("#tranPage").bs_pagination('getOption', 'currentPage')
										,$("#tranPage").bs_pagination('getOption', 'rowsPerPage'));
							} else {
								alert("删除失败")
							}
						}
					})
				}
			}
		})

		//选中复选框，点击编辑按钮跳转到编辑界面
		$("#editBtn").click(function (){
			var $xz = $("input[name=xz]:checked");

			if ($xz.length == 0){
				alert("请选择需要修改的记录")
			}else if ($xz.length > 1){
				alert("只能选择一条记录进行修改")
			}else {
				var id = $xz.val();

				window.location.href="workbench/tran/edit.do?id="+id;
			}
		})


	});

	/*
	* pageNo：页码
	* pageSize：每页展现的记录数*/
	function pageList(pageNo,pageSize){
		//将全选的复选框的√干掉
		$("#qx").prop("checked",false);

		$("#search-owner").val($.trim($("#hidden-owner").val()))
		$("#search-name").val($.trim($("#hidden-name").val()))
		$("#search-cusName").val($.trim($("#hidden-cusName").val()))
		$("#search-conName").val($.trim($("#hidden-conName").val()))

		$.ajax({
			url:"workbench/tran/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,
				"owner":$.trim($("#search-owner").val()),
				"name":$.trim($("#search-name").val()),
				"cusName":$.trim($("#search-cusName").val()),
				"conName":$.trim($("#search-conName").val())
			},
			type:"get",
			dataType:"json",
			success:function (data){
				var html = "";

				$.each(data.dataList,function (i,n){
					html += '<tr>';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/tran/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.customerId+'</td>';
					html += '<td>'+n.stage+'</td>';
					html += '<td>'+n.type+'</td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.source+'</td>';
					html += '<td>'+n.contactsId+'</td>';
					html += '</tr>';
				})

				$("#tranBody").html(html)

				//计算总页数
				var totalPages = data.total%pageSize==0 ? data.total/pageSize : parseInt(data.total/pageSize)+1;
				//数据处理完毕后，结合分页查询，对前端展现分页信息
				$("#tranPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该回调函数在点击分页组件的时候触发
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});
			}
		})
	}
</script>
</head>
<body>
<input type="hidden" id="hidden-name">
<input type="hidden" id="hidden-cusName">
<input type="hidden" id="hidden-conName">
<input type="hidden" id="hidden-owner">

	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>交易列表</h3>
			</div>
		</div>
	</div>

	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">

		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">

			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">客户名称</div>
				      <input class="form-control" type="text" id="search-cusName">
				    </div>
				  </div>

				  <br>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">阶段</div>
					  <select class="form-control">
					  	<option></option>
					  	<option>资质审查</option>
					  	<option>需求分析</option>
					  	<option>价值建议</option>
					  	<option>确定决策者</option>
					  	<option>提案/报价</option>
					  	<option>谈判/复审</option>
					  	<option>成交</option>
					  	<option>丢失的线索</option>
					  	<option>因竞争丢失关闭</option>
					  </select>
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
					  <select class="form-control">
					  	<option></option>
					  	<option>已有业务</option>
					  	<option>新业务</option>
					  </select>
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <select class="form-control" id="search-clueSource">
						  <option></option>
						  <option>广告</option>
						  <option>推销电话</option>
						  <option>员工介绍</option>
						  <option>外部介绍</option>
						  <option>在线商场</option>
						  <option>合作伙伴</option>
						  <option>公开媒介</option>
						  <option>销售邮件</option>
						  <option>合作伙伴研讨会</option>
						  <option>内部研讨会</option>
						  <option>交易会</option>
						  <option>web下载</option>
						  <option>web调研</option>
						  <option>聊天</option>
						</select>
				    </div>
				  </div>

				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">联系人名称</div>
				      <input class="form-control" type="text" id="search-conName">
				    </div>
				  </div>

				  <button type="button" class="btn btn-default" id="searchBtn">查询</button>

				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 10px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" onclick="window.location.href='workbench/tran/add.do';"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn" <%--onclick="window.location.href='workbench/transaction/edit.jsp';"--%>><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>


			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
							<td>客户名称</td>
							<td>阶段</td>
							<td>类型</td>
							<td>所有者</td>
							<td>来源</td>
							<td>联系人名称</td>
						</tr>
					</thead>
					<tbody id="tranBody">
						<%--<tr>
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/tran/detail.jsp';">动力节点-交易01</a></td>
							<td>动力节点</td>
							<td>谈判/复审</td>
							<td>新业务</td>
							<td>zhangsan</td>
							<td>广告</td>
							<td>李四</td>
						</tr>--%>

					</tbody>
				</table>
			</div>

			<div style="height: 50px; position: relative;top: 60px;">
				<div id="tranPage">

				</div>
			</div>

		</div>

	</div>
</body>
</html>
