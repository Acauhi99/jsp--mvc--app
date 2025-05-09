document.addEventListener("DOMContentLoaded", function () {
  const initRegistroForm = function () {
    const isFuncionarioCheckbox = document.getElementById("isFuncionario");
    const staffFields = document.querySelector(".staff-fields");

    if (isFuncionarioCheckbox && staffFields) {
      staffFields.style.display = isFuncionarioCheckbox.checked
        ? "block"
        : "none";

      isFuncionarioCheckbox.addEventListener("change", function () {
        staffFields.style.display = this.checked ? "block" : "none";
      });
    }
  };

  const highlightActiveMenuItem = function () {
    const currentPath = window.location.pathname;
    const menuLinks = document.querySelectorAll(".menu-items a");

    menuLinks.forEach((link) => {
      link.classList.remove("active");
    });

    let bestMatch = null;
    let longestMatch = 0;

    menuLinks.forEach((link) => {
      const linkPath = link.getAttribute("href");
      if (linkPath && currentPath.includes(linkPath)) {
        if (linkPath.length > longestMatch) {
          longestMatch = linkPath.length;
          bestMatch = link;
        }
      }
    });

    if (bestMatch) {
      bestMatch.classList.add("active");
    }
  };

  const enhanceMenuInteractions = function () {
    const menuLinks = document.querySelectorAll(".menu-items a");

    menuLinks.forEach((link) => {
      link.addEventListener("click", function (e) {
        const ripple = document.createElement("span");
        ripple.className = "ripple";
        this.appendChild(ripple);

        const rect = this.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        ripple.style.width = ripple.style.height = size + "px";
        ripple.style.left = e.clientX - rect.left - size / 2 + "px";
        ripple.style.top = e.clientY - rect.top - size / 2 + "px";

        setTimeout(() => {
          ripple.remove();
        }, 600);
      });
    });
  };

  const initMapaZoom = function () {
    const zoomContainer = document.querySelector(".mapa-zoom-container");
    if (zoomContainer) {
      zoomContainer.addEventListener("click", function (e) {
        e.preventDefault();
        zoomContainer.classList.toggle("zoomed");
      });
      document.addEventListener("click", function (e) {
        if (!zoomContainer.contains(e.target)) {
          zoomContainer.classList.remove("zoomed");
        }
      });
    }
  };

  const initConfirmationModal = function () {
    const modal = document.getElementById("confirmModal");
    if (!modal) return; // Se não houver modal na página, não faz nada

    const deleteButtons = document.querySelectorAll(".btn-delete.action-btn");
    const deleteForm = document.getElementById("deleteForm");
    const deleteId = document.getElementById("deleteId");
    const cancelButton = document.getElementById("cancelDelete");

    // Se não encontrar os elementos necessários, retorna
    if (!deleteButtons.length || !deleteForm || !deleteId || !cancelButton)
      return;

    // Configura os botões de exclusão para abrir o modal
    deleteButtons.forEach(function (button) {
      button.addEventListener("click", function (e) {
        e.preventDefault();
        const id = this.closest("form").querySelector('input[name="id"]').value;
        const action = this.closest("form").getAttribute("action");

        // Configura o modal com os dados corretos
        deleteId.value = id;
        deleteForm.action = action;

        // Personaliza mensagem se necessário (exemplo com nome de visitante)
        const row = this.closest("tr");
        if (row) {
          const name = row.querySelector("td:first-child")?.textContent;
          if (name) {
            const confirmMessage = document.getElementById("confirmMessage");
            if (confirmMessage) {
              confirmMessage.textContent = `Tem certeza que deseja excluir "${name}"?`;
            }
          }
        }

        // Mostra o modal
        modal.style.display = "flex";
      });
    });

    // Fecha o modal ao clicar em cancelar
    cancelButton.addEventListener("click", function () {
      modal.style.display = "none";
    });

    // Fecha o modal se clicar fora da caixa
    modal.addEventListener("click", function (e) {
      if (e.target === modal) {
        modal.style.display = "none";
      }
    });
  };

  initRegistroForm();
  highlightActiveMenuItem();
  enhanceMenuInteractions();
  initMapaZoom();
  initConfirmationModal();
});
