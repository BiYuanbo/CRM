<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

	Map<String,String> pMap = (Map<String, String>) application.getAttribute("pMap");

	Set<String> set = pMap.keySet();

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

	<script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>

	<script type="text/javascript">
		$(function (){
			//日历框架
			$(".time1").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "bottom-left"
			});
			$(".time2").datetimepicker({
				minView: "month",
				language:  'zh-CN',
				format: 'yyyy-mm-dd',
				autoclose: true,
				todayBtn: true,
				pickerPosition: "top-left"
			});

			//自动补全框架
			$("#edit-customerName").typeahead({
				source: function (query, process) {
					$.get(
							"workbench/tran/getCustomerName.do",
							{ "name" : query },
							function (data) {
								//alert(data);

								/*
                                * data:[{客户名称1}，{2}]，{3}*/
								process(data);
							},
							"json"
					);
				},
				delay: 500
			});

			//将‘阶段-可能性’map格式转换为json格式
			var json = {
				<%
                    for (String key : set) {
                        String value = pMap.get(key);
                %>

				"<%=key%>":"<%=value%>",

				<%
                    }
                %>
			};

			//为阶段下拉框，绑定选中下拉框的事件，根据选中的阶段填写可能性
			$("#edit-stage").change(function (){
				var stage = $("#edit-stage").val();

				/*
                    * 以json.key的形式不能取得value
                    * 因为今天的stage是一个可变的变量
                    * 如果是这样的key，那么我们就不能以传统的json.key的形式来取直
                    * 我们要使用的取值方式为：json[key]*/
				var possibility = json[stage];

				$("#edit-possibility").val(possibility)
			})

			//为放大镜图标绑定事件，打开搜索市场活动模态窗口
			$("#openSearchModalBtn1").click(function (){
				$("#findMarketActivity").modal("show")
			})
			//为市场活动模态窗口搜索框绑定事件
			$("#aname").keydown(function (e){
				if (e.keyCode == 13){
					$.ajax({
						url:"workbench/tran/getActivityListByName.do",
						data:{
							"aname":$.trim($("#aname").val())
						},
						type:"get",
						dataType:"json",
						success:function (data){
							var html = "";

							$.each(data,function (i,n){
								html += '<tr>';
								html += '<td><input type="radio" name="axz" value="'+n.id+'"/></td>';
								html += '<td id="'+n.id+'">'+n.name+'</td>';
								html += '<td>'+n.startDate+'</td>';
								html += '<td>'+n.endDate+'</td>';
								html += '<td>'+n.owner+'</td>';
								html += '</tr>';
							})

							$("#activitySearchBody").html(html);
						}
					})

					return false;
				}
			})
			//为 提交（市场活动） 按钮绑定事件，填充市场活动源（填写两项信息 名字+id）
			$("#submitActivityBtn").click(function (){
				var $xz = $("input[name=axz]:checked");
				var id = $xz.val();

				var name = $("#"+id).html();

				$("#edit-activityName").val(name);
				$("#activityId").val(id);

				$("#findMarketActivity").modal("hide")
			})

			//为放大镜图标绑定事件，打开搜索联系人名称模态窗口
			$("#openSearchModalBtn2").click(function (){
				$("#findContacts").modal("show")
			})
			//为联系人名称模态窗口搜索框绑定事件
			$("#cname").keydown(function (e){
				if (e.keyCode == 13){

					$.ajax({
						url: "workbench/tran/getContactsListByName.do",
						data: {
							"cname":$.trim($("#cname").val())
						},
						type: "get",
						dataType: "json",
						success:function (data){
							var html = "";

							$.each(data,function (i,n){
								html += '<tr>';
								html += '<td><input type="radio" name="cxz" value="'+n.id+'"/></td>';
								html += '<td id="'+n.id+'">'+n.fullname+'</td>';
								html += '<td>'+n.email+'</td>';
								html += '<td>'+n.mphone+'</td>';
								html += '</tr>';
							})

							$("#contactsSearchBody").html(html)
						}
					})

					return false;
				}
			})
			//为 提交（联系人名称） 按钮绑定事件，填充联系人名称（填写两项信息 名字+id）
			$("#submitContactsBtn").click(function (){
				var $xz = $("input[name=cxz]:checked")
				var id = $xz.val();

				var name = $("#"+id).html();

				$("#edit-contactsName").val(name);
				$("#contactsId").val(id);

				$("#findContacts").modal("hide")
			})

			//为更新按钮绑定事件
			$("#updateBtn").click(function (){
				$("#tranForm").submit();
			})

			//为取消按钮绑定事件
			$("#cancelBtn").click(function (){
				window.location.href = 'workbench/transaction/index.jsp';
			})
		})
	</script>
</head>
<body>

<!-- 查找市场活动 -->
<div class="modal fade" id="findMarketActivity" role="dialog">
	<div class="modal-dialog" role="document" style="width: 80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title">查找市场活动</h4>
			</div>
			<div class="modal-body">
				<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
					<form class="form-inline" role="form">
						<div class="form-group has-feedback">
							<input type="text" id="aname" class="form-control" style="width: 300px;" placeholder="请输入市场活动名称，支持模糊查询">
							<span class="glyphicon glyphicon-search form-control-feedback"></span>
						</div>
					</form>
				</div>
				<table id="activityTable3" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
					<thead>
					<tr style="color: #B3B3B3;">
						<td></td>
						<td>名称</td>
						<td>开始日期</td>
						<td>结束日期</td>
						<td>所有者</td>
					</tr>
					</thead>
					<tbody id="activitySearchBody">
					<%--<tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>发传单</td>
                        <td>2020-10-10</td>
                        <td>2020-10-20</td>
                        <td>zhangsan</td>
                    </tr>--%>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-primary" id="submitActivityBtn">提交</button>
			</div>
		</div>
	</div>
</div>

<!-- 查找联系人 -->
<div class="modal fade" id="findContacts" role="dialog">
	<div class="modal-dialog" role="document" style="width: 80%;">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">
					<span aria-hidden="true">×</span>
				</button>
				<h4 class="modal-title">查找联系人</h4>
			</div>
			<div class="modal-body">
				<div class="btn-group" style="position: relative; top: 18%; left: 8px;">
					<form class="form-inline" role="form">
						<div class="form-group has-feedback">
							<input type="text" class="form-control" id="cname" style="width: 300px;" placeholder="请输入联系人名称，支持模糊查询">
							<span class="glyphicon glyphicon-search form-control-feedback"></span>
						</div>
					</form>
				</div>
				<table id="activityTable" class="table table-hover" style="width: 900px; position: relative;top: 10px;">
					<thead>
					<tr style="color: #B3B3B3;">
						<td></td>
						<td>名称</td>
						<td>邮箱</td>
						<td>手机</td>
					</tr>
					</thead>
					<tbody id="contactsSearchBody">
					<%--<tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>李四</td>
                        <td>lisi@bjpowernode.com</td>
                        <td>12345678901</td>
                    </tr>
                    <tr>
                        <td><input type="radio" name="activity"/></td>
                        <td>李四</td>
                        <td>lisi@bjpowernode.com</td>
                        <td>12345678901</td>
                    </tr>--%>
					</tbody>
				</table>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-primary" id="submitContactsBtn">提交</button>
			</div>
		</div>
	</div>
</div>


<div style="position:  relative; left: 30px;">
	<h3>修改交易</h3>
	<div style="position: relative; top: -40px; left: 70%;">
		<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
		<button type="button" class="btn btn-default" id="cancelBtn">取消</button>
	</div>
	<hr style="position: relative; top: -40px;">
</div>
<form action="workbench/tran/update.do" id="tranForm" method="post" class="form-horizontal" role="form" style="position: relative; top: -30px;">
	<input type="hidden" name="id" value="${t.id}">

	<div class="form-group">
		<label for="edit-transactionOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-owner" name="owner">
				<option>${t.owner}</option>
			</select>
		</div>
		<label for="edit-amountOfMoney" class="col-sm-2 control-label">金额</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-oney" name="money" value="${t.money}">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-transactionName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-name" name="name" value="${t.name}">
		</div>
		<label for="edit-expectedClosingDate" class="col-sm-2 control-label">预计成交日期<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control time1" id="edit-expectedDate" name="expectedDate" value="${t.expectedDate}">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-accountName" class="col-sm-2 control-label">客户名称<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-customerName" name="customerName" value="${t.customerId}" placeholder="支持自动补全，输入客户不存在则新建">
		</div>
		<label for="edit-transactionStage" class="col-sm-2 control-label">阶段<span style="font-size: 15px; color: red;">*</span></label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-stage" name="stage">
				<option></option>
				<c:forEach items="${stage}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
			</select>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-transactionType" class="col-sm-2 control-label">类型</label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-transactionType" name="type">
				<option></option>
				<c:forEach items="${transactionType}" var="t">
					<option value="${t.value}">${t.text}</option>
				</c:forEach>
			</select>
		</div>
		<label for="edit-possibility" class="col-sm-2 control-label">可能性</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-possibility">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-clueSource" class="col-sm-2 control-label">来源</label>
		<div class="col-sm-10" style="width: 300px;">
			<select class="form-control" id="edit-clueSource" name="source">
				<option></option>
				<c:forEach items="${source}" var="s">
					<option value="${s.value}">${s.text}</option>
				</c:forEach>
			</select>
		</div>
		<label for="edit-activitySrc" class="col-sm-2 control-label">市场活动源&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn1"><span class="glyphicon glyphicon-search"></span></a></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-activityName" value="${t.activityId}">
			<input type="hidden" id="activityId" name="activityId">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-contactsName" class="col-sm-2 control-label">联系人名称&nbsp;&nbsp;<a href="javascript:void(0);" id="openSearchModalBtn2"><span class="glyphicon glyphicon-search"></span></a></label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control" id="edit-contactsName" value="${t.contactsId}">
			<input type="hidden" id="contactsId" name="contactsId">
		</div>
	</div>

	<div class="form-group">
		<label for="edit-describe" class="col-sm-2 control-label">描述</label>
		<div class="col-sm-10" style="width: 70%;">
			<textarea class="form-control" rows="3" id="edit-description" name="description">${t.description}</textarea>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-contactSummary" class="col-sm-2 control-label">联系纪要</label>
		<div class="col-sm-10" style="width: 70%;">
			<textarea class="form-control" rows="3" id="edit-contactSummary" name="contactSummary">${t.contactSummary}</textarea>
		</div>
	</div>

	<div class="form-group">
		<label for="edit-nextContactTime" class="col-sm-2 control-label">下次联系时间</label>
		<div class="col-sm-10" style="width: 300px;">
			<input type="text" class="form-control time2" id="edit-nextContactTime" name="nextContactTime" value="${t.nextContactTime}">
		</div>
	</div>

</form>
</body>
</html>
