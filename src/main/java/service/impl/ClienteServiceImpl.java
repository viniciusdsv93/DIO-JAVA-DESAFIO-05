package service.impl;

import model.Cliente;
import model.ClienteRepository;
import model.Endereco;
import model.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.ClienteService;
import service.ViaCepService;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

  @Autowired
  private ClienteRepository clienteRepository;

  @Autowired
  private EnderecoRepository enderecoRepository;

  @Autowired
  private ViaCepService viaCepService;

  @Override
  public Iterable<Cliente> buscarTodos() {
    return clienteRepository.findAll();
  }

  @Override
  public Cliente buscarPorId(Long id) {
    Optional<Cliente> cliente = clienteRepository.findById(id);
    return cliente.get();
  }

  @Override
  public void inserir(Cliente cliente) {
    salvarClienteComCep(cliente);
  }

  private void salvarClienteComCep(Cliente cliente) {
    // Verificar se o endereco do cliente já existe, pelo CEP
    String cep = cliente.getEndereco().getCep();
    Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
      // Caso nao exista, integrar com o ViaCep e persistir o retorno
      Endereco novoEndereco = viaCepService.consultarCep(cep);
      enderecoRepository.save(novoEndereco);
      return novoEndereco;
    });
    cliente.setEndereco(endereco);
    clienteRepository.save(cliente);
  }

  @Override
  public void atualizar(Long id, Cliente cliente) {
    // Buscar cliente por ID caso exista
    Optional<Cliente> clienteBD = clienteRepository.findById(id);
    if (clienteBD.isPresent()) {
      salvarClienteComCep(cliente);
    }
  }

  @Override
  public void deletar(Long id) {
    clienteRepository.deleteById(id);
  }
}
