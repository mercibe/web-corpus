<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<!-- <script type="text/javascript" src="/js/jquery-1.6.1.min.js"></script>          -->
<!-- <script src="/js/jquery-ui-1.8.1.custom.min.js" type="text/javascript"></script> -->
<!-- <script src="/js/jquery.layout.js" type="text/javascript"></script> -->
<!-- <script src="/js/i18n/grid.locale-en.js" type="text/javascript"></script> -->
<!--  <script type="text/javascript"> -->
<!-- 	$.jgrid.no_legacy_api = true; -->
<!-- 	$.jgrid.useJSON = true; -->
<!-- </script>   -->
<!-- <script src="/js/ui.multiselect.js" type="text/javascript"></script> -->
<!-- <script src="/js/jquery.jqGrid.min.js" type="text/javascript"></script> -->
<!-- <script src="/js/jquery.tablednd.js" type="text/javascript"></script> -->
<!-- <script src="/js/jquery.contextmenu.js" type="text/javascript"></script> -->
 
 
    <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.2/themes/redmond/jquery-ui.css" />
    <link rel="stylesheet" type="text/css" href="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/css/ui.jqgrid.css" />
    <link rel="stylesheet" type="text/css" href="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/css/jquery.searchFilter.css" />

    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.js"></script>

    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/i18n/grid.locale-en.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.base.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.common.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.formedit.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.inlinedit.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.custom.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/jquery.fmatter.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.grouping.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/grid.jqueryui.js"></script>
    <script type="text/javascript" src="http://www.ok-soft-gmbh.com/jqGrid/jqGrid-3.8.beta-20100824/js/jquery.searchFilter.js"></script>
 
 <script type="text/javascript">    
 
 
 
 $(document).ready(function() {
  
     var grid = $("#jsonmap");
     
	 grid.jqGrid({        
		   	url:'/corpus/mot/tous',
			datatype: "json",
		   	colNames:['Lemme','Mot', 'Catgram', 'Genre','Nombre','Précision','RO'],
		   	colModel:[
		   		{name:'lemme',index:'lemme', width:55},
		   		{name:'mot',index:'mot', width:90, jsonmap:"invdate"},
		   		{name:'catgram',index:'catgram', width:100},
		   		{name:'genre',index:'genre', width:80, align:"right"},
		   		{name:'nombre',index:'nombre', width:80, align:"right"},		
		   		{name:'catgramPrécision',index:'catgramPrécision', width:80,align:"right"},		
		   		{name:'ro',index:'ro', width:150, sortable:false}		
		   	],
		   	rowNum:10,
		   	rowList:[10,20,30],
		   	pager: '#pjmap',
		   	sortname: 'lemme',
		    viewrecords: true,
		    sortorder: "desc",
			jsonReader: {
				repeatitems : false,
				id: "0"
			},
			caption: "JSON Mapping",
			height: '100%'
		});
		grid.jqGrid('navGrid','#pjmap',{edit:false,add:false,del:false});

     
   
 });
   
 
 </script>     
                                 
<title>web-corpus</title>
</head>
<body>

<h1>web-corpus</h1>

<table id="jsonmap"></table>
<div id="pjmap"></div>

<ul>

</ul>
Listes = ${listes}
</body>
</html>