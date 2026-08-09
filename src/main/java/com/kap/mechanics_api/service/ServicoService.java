package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.*;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.ServicoNaoEncontradoException;
import com.kap.mechanics_api.mapper.ServicoMapper;
import com.kap.mechanics_api.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;

    public ServicoService(
            ServicoRepository servicoRepository,
            ServicoMapper servicoMapper) {

        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper;
    }

    public ServicoResponseDTO cadastrar(CriacaoServicoRequestDTO dto) {

        Servico servico = servicoMapper.toEntity(dto);

        servico = servicoRepository.save(servico);

        return servicoMapper.toResponseDto(servico);
    }

    public List<ServicoResponseDTO> listar() {

        return servicoMapper.toListagemDto(
                servicoRepository.findAll());
    }

    public Servico pesquisarPorId(Integer id) {

        return servicoRepository.findById(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }

    public ServicoResponseDTO buscarPorId(Integer id) {

        return servicoMapper.toListagemServicoResponseDto(
                pesquisarPorId(id));
    }

    public void deletar(Integer id) {

        Servico servico = pesquisarPorId(id);

        servicoRepository.delete(servico);
    }

    public ServicoResponseDTO atualizar(
            AtualizacaoServicoRequestDTO dto,
            Integer id) {

        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(
                    AtualizacaoServicoRequestDTO.class);
        }

        Servico servico = pesquisarPorId(id);

        if (StringUtils.hasText(dto.nome())) {
            servico.setNome(dto.nome());
        }

        if (StringUtils.hasText(dto.descricao())) {
            servico.setDescricao(dto.descricao());
        }

        if (dto.valorMaoDeObra() != null) {
            servico.setValorMaoDeObra(dto.valorMaoDeObra());
        }

        if (dto.tempoEstimadoMin() != null) {
            servico.setTempoEstimadoMin(dto.tempoEstimadoMin());
        }

        if (dto.ativo() != null) {
            servico.setAtivo(dto.ativo());
        }

        Servico servicoAlterado = servicoRepository.save(servico);

        return servicoMapper.toAtualizacaoServicoResponseDto(servicoAlterado);
    }
}
