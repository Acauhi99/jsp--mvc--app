package handlers;

import models.Ingresso;
import models.Ingresso.TipoIngresso;
import models.Customer;
import repositories.IngressoRepository;
import repositories.CustomerRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class IngressoHandler {

  private final IngressoRepository ingressoRepository;
  private final CustomerRepository customerRepository;

  public IngressoHandler(IngressoRepository ingressoRepository, CustomerRepository customerRepository) {
    this.ingressoRepository = ingressoRepository;
    this.customerRepository = customerRepository;
  }

  public List<Ingresso> listarTodosIngressos() {
    return ingressoRepository.findAll();
  }

  public List<Ingresso> listarIngressosPorComprador(UUID compradorId) {
    return ingressoRepository.findByCompradorId(compradorId);
  }

  public List<Ingresso> listarIngressosPorStatus(boolean utilizado) {
    return ingressoRepository.findByUtilizado(utilizado);
  }

  public List<Ingresso> listarIngressosPorTipo(TipoIngresso tipo) {
    return ingressoRepository.findByTipo(tipo);
  }

  public Optional<Ingresso> buscarIngressoPorId(String idStr) {
    if (idStr == null || idStr.isEmpty()) {
      return Optional.empty();
    }

    try {
      UUID id = UUID.fromString(idStr);
      return ingressoRepository.findById(id);
    } catch (IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  public List<Ingresso> filtrarEOrdenarIngressos(List<Ingresso> ingressos, String status,
      String precoOrder, String dataOrder) {
    List<Ingresso> result = new ArrayList<>(ingressos);

    if (status != null && !status.isEmpty()) {
      if ("UTILIZADO".equalsIgnoreCase(status)) {
        result = result.stream().filter(Ingresso::isUtilizado).collect(Collectors.toList());
      } else if ("DISPONIVEL".equalsIgnoreCase(status)) {
        result = result.stream().filter(i -> !i.isUtilizado()).collect(Collectors.toList());
      }
    }

    if ("asc".equalsIgnoreCase(precoOrder)) {
      result.sort(Comparator.comparing(Ingresso::getValor));
    } else if ("desc".equalsIgnoreCase(precoOrder)) {
      result.sort(Comparator.comparing(Ingresso::getValor).reversed());
    }

    if ("asc".equalsIgnoreCase(dataOrder)) {
      result.sort(Comparator.comparing(Ingresso::getDataCompra));
    } else if ("desc".equalsIgnoreCase(dataOrder)) {
      result.sort(Comparator.comparing(Ingresso::getDataCompra).reversed());
    }

    return result;
  }

  public List<Ingresso> paginarIngressos(List<Ingresso> ingressos, int page, int pageSize) {
    int total = ingressos.size();
    int from = (page - 1) * pageSize;
    int to = Math.min(from + pageSize, total);

    if (from < total) {
      return ingressos.subList(from, to);
    } else {
      return new ArrayList<>();
    }
  }

  public PageResult<Ingresso> processarIngressosPaginados(List<Ingresso> ingressos, String status,
      String precoOrder, String dataOrder,
      int page, int pageSize) {
    List<Ingresso> filtered = filtrarEOrdenarIngressos(ingressos, status, precoOrder, dataOrder);
    int totalPages = (int) Math.ceil((double) filtered.size() / pageSize);
    List<Ingresso> paginatedItems = paginarIngressos(filtered, page, pageSize);

    return new PageResult<>(paginatedItems, page, totalPages);
  }

  public boolean temPermissaoParaVerIngresso(Ingresso ingresso, UUID userId, boolean isAdmin) {
    if (isAdmin) {
      return true;
    }

    return ingresso.getComprador() != null &&
        ingresso.getComprador().getId().equals(userId);
  }

  public double calcularValorIngresso(TipoIngresso tipo) {
    switch (tipo) {
      case ADULTO:
        return 50.0;
      case CRIANCA:
        return 25.0;
      case IDOSO:
        return 20.0;
      case ESTUDANTE:
        return 30.0;
      case DEFICIENTE:
        return 0.0;
      default:
        return 50.0;
    }
  }

  public List<Ingresso> comprarIngressos(String tipoStr, int quantidade, UUID compradorId) throws Exception {
    if (quantidade < 1 || quantidade > 10) {
      throw new Exception("Quantidade inválida. Mínimo: 1, Máximo: 10");
    }

    TipoIngresso tipo;
    try {
      tipo = TipoIngresso.valueOf(tipoStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("Tipo de ingresso inválido");
    }

    Optional<Customer> optComprador = customerRepository.findById(compradorId);
    if (optComprador.isEmpty()) {
      throw new Exception("Comprador não encontrado");
    }

    Customer comprador = optComprador.get();
    double valor = calcularValorIngresso(tipo);
    List<Ingresso> ingressosComprados = new ArrayList<>();

    for (int i = 0; i < quantidade; i++) {
      Ingresso ingresso = Ingresso.create(tipo, valor, comprador);
      ingressoRepository.save(ingresso);
      ingressosComprados.add(ingresso);
    }

    return ingressosComprados;
  }

  public Ingresso utilizarIngresso(String ingressoIdStr, UUID userId) throws Exception {
    if (ingressoIdStr == null || ingressoIdStr.isEmpty()) {
      throw new Exception("ID do ingresso não fornecido");
    }

    UUID ingressoId;
    try {
      ingressoId = UUID.fromString(ingressoIdStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de ingresso inválido");
    }

    Optional<Ingresso> optIngresso = ingressoRepository.findById(ingressoId);
    if (optIngresso.isEmpty()) {
      throw new Exception("Ingresso não encontrado");
    }

    Ingresso ingresso = optIngresso.get();

    if (ingresso.getComprador() == null || !ingresso.getComprador().getId().equals(userId)) {
      throw new Exception("Você não tem permissão para utilizar este ingresso");
    }

    if (ingresso.isUtilizado()) {
      throw new Exception("Este ingresso já foi utilizado");
    }

    ingresso.setUtilizado(true);
    ingressoRepository.update(ingresso);

    return ingresso;
  }

  public Ingresso alternarStatusIngresso(String ingressoIdStr) throws Exception {
    if (ingressoIdStr == null || ingressoIdStr.isEmpty()) {
      throw new Exception("ID do ingresso não fornecido");
    }

    UUID ingressoId;
    try {
      ingressoId = UUID.fromString(ingressoIdStr);
    } catch (IllegalArgumentException e) {
      throw new Exception("ID de ingresso inválido");
    }

    Optional<Ingresso> optIngresso = ingressoRepository.findById(ingressoId);
    if (optIngresso.isEmpty()) {
      throw new Exception("Ingresso não encontrado");
    }

    Ingresso ingresso = optIngresso.get();
    ingresso.setUtilizado(!ingresso.isUtilizado());

    ingressoRepository.update(ingresso);
    return ingresso;
  }

  public static class PageResult<T> {
    private final List<T> items;
    private final int currentPage;
    private final int totalPages;

    public PageResult(List<T> items, int currentPage, int totalPages) {
      this.items = items;
      this.currentPage = currentPage;
      this.totalPages = totalPages;
    }

    public List<T> getItems() {
      return items;
    }

    public int getCurrentPage() {
      return currentPage;
    }

    public int getTotalPages() {
      return totalPages;
    }
  }
}
