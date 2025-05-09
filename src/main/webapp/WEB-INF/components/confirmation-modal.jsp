<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div id="confirmModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background-color:rgba(0,0,0,0.5); z-index:1000; justify-content:center; align-items:center;">
    <div style="background:white; border-radius:5px; padding:20px; max-width:400px; text-align:center; box-shadow:0 2px 10px rgba(0,0,0,0.1);">
        <h3 style="margin-bottom:15px;">Confirmar exclusão</h3>
        <p id="confirmMessage">Tem certeza que deseja excluir este item?</p>
        <div style="margin-top:20px; display:flex; justify-content:center; gap:10px;">
            <button id="cancelDelete" class="btn btn-outline">Cancelar</button>
            <form id="deleteForm" method="post">
                <input type="hidden" name="id" id="deleteId">
                <button type="submit" class="btn btn-delete">Confirmar</button>
            </form>
        </div>
    </div>
</div>