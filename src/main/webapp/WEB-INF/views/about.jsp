<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:master title="Sobre Nós">
    <section class="container about-page-section" style="padding-top: 3rem; padding-bottom: 3rem;">
        <h1 class="section-title">Sobre o Zoo Park</h1>

        <div class="about-intro" style="text-align: center; margin-bottom: 3rem; font-size: 1.1em;">
            <p>Bem-vindo ao Zoo Park, um refúgio dedicado à celebração e conservação da vida selvagem. Fundado em [Ano de Fundação], nosso parque cresceu e se tornou um centro líder em educação ambiental, pesquisa e bem-estar animal.</p>
        </div>

        <div class="about-grid" style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 2rem; margin-bottom: 3rem;">
            <div class="about-card" style="background: white; padding: 1.5rem; border-radius: var(--border-radius); box-shadow: var(--box-shadow);">
                <h2>Nossa Missão</h2>
                <p>Conectar pessoas com a natureza, inspirando a conservação da biodiversidade através da educação, pesquisa e experiências memoráveis com os animais.</p>
            </div>
            <div class="about-card" style="background: white; padding: 1.5rem; border-radius: var(--border-radius); box-shadow: var(--box-shadow);">
                <h2>Nossa Visão</h2>
                <p>Ser um zoológico de referência mundial, reconhecido pela excelência no cuidado animal, programas de conservação inovadores e impacto positivo na comunidade e no meio ambiente.</p>
            </div>
        </div>

        <div class="conservation-focus" style="margin-bottom: 3rem;">
            <h2>Foco em Conservação</h2>
            <p>A conservação está no coração de tudo o que fazemos. Participamos ativamente de programas de reprodução de espécies ameaçadas, apoiamos projetos de conservação in-situ (no habitat natural) e trabalhamos para aumentar a conscientização sobre os desafios enfrentados pela vida selvagem.</p>
            <p>Nossas instalações são projetadas para simular habitats naturais, garantindo o mais alto padrão de bem-estar para nossos residentes animais.</p>
        </div>

        <div class="visitor-experience" style="text-align: center;">
            <h2>Sua Visita</h2>
            <p>Oferecemos uma experiência educativa e divertida para visitantes de todas idades. Explore nossos diversos habitats, participe de palestras educativas e descubra a incrível diversidade do reino animal. Cada visita apoia diretamente nossos esforços de cuidado e conservação.</p>
            <img src="${pageContext.request.contextPath}/images/zoologico_982374.jpg" alt="Visitantes no Zoo Park" style="max-width: 70%; height: auto; margin-top: 1.5rem; border-radius: var(--border-radius); box-shadow: var(--box-shadow);">
        </div>

    </section>
</t:master>