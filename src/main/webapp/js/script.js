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
      const linkPath = link.getAttribute("href");
      if (linkPath && currentPath.includes(linkPath)) {
        link.classList.add("active");
      }
    });
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

  initRegistroForm();
  highlightActiveMenuItem();
  enhanceMenuInteractions();
});
