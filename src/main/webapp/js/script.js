document.addEventListener("DOMContentLoaded", function () {
  const isFuncionarioCheckbox = document.getElementById("isFuncionario");
  const staffFields = document.querySelector(".staff-fields");

  if (isFuncionarioCheckbox && staffFields) {
    isFuncionarioCheckbox.addEventListener("change", function () {
      staffFields.style.display = this.checked ? "block" : "none";
    });
  }
});
