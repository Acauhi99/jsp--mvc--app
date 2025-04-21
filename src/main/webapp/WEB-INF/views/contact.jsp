<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Contato">
    <section class="container contact-page-section" style="padding-top: 3rem; padding-bottom: 3rem;">
        <h1 class="section-title">Entre em Contato</h1>

        <div class="contact-grid" style="display: grid; grid-template-columns: 1fr; gap: 3rem; align-items: start;">

            <div class="contact-info" style="background: white; padding: 2rem; border-radius: var(--border-radius); box-shadow: var(--box-shadow);">
                <h2>Informações de Contato</h2>
                <p>Estamos aqui para ajudar! Entre em contato conosco através dos seguintes canais:</p>
                <ul style="list-style: none; padding: 0; margin-top: 1.5rem;">
                    <li style="margin-bottom: 1rem;"><strong><i class="fas fa-map-marker-alt"></i> Endereço:</strong><br> Alameda dos Animais, 123, Cidade Selvagem, CEP 12345-678</li>
                    <li style="margin-bottom: 1rem;"><strong><i class="fas fa-phone"></i> Telefone:</strong><br> +123 456 7890</li>
                    <li style="margin-bottom: 1rem;"><strong><i class="fas fa-envelope"></i> Email Geral:</strong><br> info@zoopark.com</li>
                    <li style="margin-bottom: 1rem;"><strong><i class="fas fa-ticket-alt"></i> Ingressos e Reservas:</strong><br> tickets@zoopark.com</li>
                </ul>

                <h3 style="margin-top: 2rem; margin-bottom: 1rem;">Horário de Funcionamento</h3>
                <p>Segunda - Sexta: 9:00 - 18:00</p>
                <p>Sábado - Domingo e Feriados: 10:00 - 20:00</p>
            </div>
        </div>
    </section>
</t:master>