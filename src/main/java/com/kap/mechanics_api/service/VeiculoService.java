package com.kap.mechanics_api.service;


import com.kap.mechanics_api.domain.Cliente;
import com.kap.mechanics_api.domain.ClienteVeiculo;
import com.kap.mechanics_api.domain.Veiculo;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.VeiculoNaoEncontradoException;
import com.kap.mechanics_api.mapper.VeiculoMapper;
import com.kap.mechanics_api.repository.ClienteVeiculoRepository;
import com.kap.mechanics_api.repository.VeiculoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final VeiculoMapper veiculoMapper;
    private final ClienteVeiculoRepository clienteVeiculoRepository;
    private final ClienteService clienteService;

    public VeiculoService(VeiculoRepository veiculoRepository, VeiculoMapper mapper, ClienteVeiculoRepository clienteVeiculoRepository,
        ClienteService clienteService){
        this.veiculoRepository = veiculoRepository;
        this.veiculoMapper = mapper;
        this.clienteVeiculoRepository = clienteVeiculoRepository;
        this.clienteService = clienteService;

    }

    @Transactional
    public CriacaoVeiculoResponseDTO cadastrar(
            @Valid CriacaoVeiculoRequestDTO dto
    ) {

        Veiculo veiculo = veiculoMapper.toEntity(dto);

        veiculo.setDataCriacao(LocalDateTime.now());

        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        persistirVinculoClienteVeiculo(
                dto.clienteId(),
                veiculoSalvo
        );

        return veiculoMapper.toResponseDto(veiculoSalvo);
    }

    private ClienteVeiculo persistirVinculoClienteVeiculo(
            Integer idCliente,
            Veiculo veiculo
    ) {

        Cliente cliente = clienteService.pesquisarPorId(idCliente);

        ClienteVeiculo clienteVeiculo =
                new ClienteVeiculo(veiculo, cliente);

        return clienteVeiculoRepository.save(clienteVeiculo);
    }

    public List<ListagemVeiculoResponseDTO> listar() {

        List<Veiculo> veiculos = veiculoRepository.findAll();

        return veiculoMapper.toListagemDto(veiculos);
    }

    public Veiculo pesquisarPorId(Integer id) {

        return veiculoRepository.findById(id)
                .orElseThrow(() -> new VeiculoNaoEncontradoException(id));
    }

    public ListagemVeiculoResponseDTO buscarPorId(Integer id) {

        Veiculo veiculo = pesquisarPorId(id);

        return veiculoMapper.toListagemVeiculoResponseDto(veiculo);
    }

    public void deletar(Integer id) {

        Veiculo veiculo = pesquisarPorId(id);

        veiculoRepository.delete(veiculo);
    }

    @Transactional
    public AtualizacaoVeiculoResponseDTO atualizar(
            AtualizacaoVeiculoRequestDTO dto,
            Integer id
    ) {

        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(
                    AtualizacaoVeiculoRequestDTO.class
            );
        }

        Veiculo veiculo = pesquisarPorId(id);

        atualizarCamposVeiculo(dto, veiculo);

        Veiculo veiculoAlterado =
                veiculoRepository.save(veiculo);

        if (dto.clienteId() != null) {
            persistirVinculoClienteVeiculo(
                    dto.clienteId(),
                    veiculoAlterado
            );
        }

        return veiculoMapper.toAtualizacaoVeiculoResponseDto(
                veiculoAlterado
        );
    }

    private void atualizarCamposVeiculo(
            AtualizacaoVeiculoRequestDTO dto,
            Veiculo veiculo
    ) {

        if (dto.ano() != null) {
            veiculo.setAno(dto.ano());
        }

        if (StringUtils.hasText(dto.placa())) {
            veiculo.setPlaca(dto.placa());
        }

        if (StringUtils.hasText(dto.marca())) {
            veiculo.setMarca(dto.marca());
        }

        if (StringUtils.hasText(dto.modelo())) {
            veiculo.setModelo(dto.modelo());
        }
    }
}
