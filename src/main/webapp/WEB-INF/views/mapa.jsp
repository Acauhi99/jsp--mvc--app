<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<t:master title="Mapa do Zoológico">
    <section class="mapa-section" style="padding:3rem 0;">
        <div class="container">
            <h1 class="section-title">Mapa do Zoológico</h1>
            <div style="display:flex;justify-content:center;">
                <a href="#" tabindex="0" class="mapa-zoom-container">
                    <img src="${pageContext.request.contextPath}/images/mapa-zoo-3002562055.jpg"
                         alt="Mapa ilustrado do zoológico"
                         class="mapa-img">
                </a>
            </div>
            <div style="max-width:700px;margin:2.5rem auto 0 auto;">
                <div class="alert alert-error" style="font-size:1.1rem;">
                    ⚠️ <strong>Importante:</strong> Para o bem-estar dos animais, não os alimente e não ultrapasse as barreiras de proteção.
                </div>
            </div>
            <div style="max-width:700px;margin:2.5rem auto 0 auto;text-align:justify;font-size:1.15rem;line-height:1.7;">
                <p>
                    Explore o nosso zoológico com este mapa ilustrado que apresenta a localização dos principais animais, atrações e serviços.
                    O mapa está dividido em áreas temáticas onde você pode encontrar:
                </p>
                <ul style="margin:1.5rem 0 1.5rem 2rem;padding:0;list-style:disc;">
                    <li>Mamíferos de grande porte, como elefantes, girafas, ursos, rinocerontes e camelos.</li>
                    <li>Primatas e felinos, incluindo chimpanzés, leões, leopardos e tigres.</li>
                    <li>Aves e répteis, localizados em áreas dedicadas ao canto e tranquilidade, como a seção <strong>"Quebra do Silêncio"</strong>.</li>
                    <li>Serviços essenciais, como bilheteria, banheiros, lanchonetes, áreas de descanso e enfermaria, todos sinalizados com ícones azuis.</li>
                    <li>Espaços interativos para crianças, incluindo carrossel e áreas de alimentação.</li>
                </ul>
                <p>
                    A entrada principal está localizada na parte inferior direita do mapa, próxima à bilheteria e à praça de alimentação.
                    As trilhas são bem demarcadas, facilitando sua navegação pelo parque.
                </p>
            </div>
        </div>
    </section>
</t:master>