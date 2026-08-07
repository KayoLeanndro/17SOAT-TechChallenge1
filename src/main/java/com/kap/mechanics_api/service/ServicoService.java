package com.kap.mechanics_api.service;

import com.kap.mechanics_api.domain.Servico;
import com.kap.mechanics_api.dto.servico.*;
import com.kap.mechanics_api.dto.veiculo.*;
import com.kap.mechanics_api.exception.NenhumCampoInformadoException;
import com.kap.mechanics_api.exception.ServicoNaoEncontradoException;
import com.kap.mechanics_api.mapper.ServicoMapper;
import com.kap.mechanics_api.repository.ServicoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

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

    public CriacaoServicoResponseDTO cadastrar(
            @Valid CriacaoServicoRequestDTO dto) {

        Servico servico = servicoMapper.toEntity(dto);

        servico = servicoRepository.save(servico);

        return servicoMapper.toResponseDto(servico);
    }

    public List<ListagemServicoResponseDTO> listar() {

        return servicoMapper.toListagemDto(
                servicoRepository.findAll());
    }

    public Servico pesquisarPorId(Integer id) {

        return servicoRepository.findById(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }

    public ListagemServicoResponseDTO buscarPorId(Integer id) {

        return servicoMapper.toListagemServicoResponseDto(
                pesquisarPorId(id));
    }

    public void deletar(Integer id) {

        Servico servico = pesquisarPorId(id);

        servicoRepository.delete(servico);
    }

    public AtualizacaoServicoResponseDTO atualizar(
            AtualizacaoServicoRequestDTO dto,
            Integer id) {

        if (!dto.temAoMenosUmCampoPreenchido()) {
            throw new NenhumCampoInformadoException(
                    AtualizacaoServicoRequestDTO.class);
        }

        Servico servico = pesquisarPorId(id);

        Servico servicoAlterado = servicoRepository.save(servico);

        return servicoMapper.toAtualizacaoServicoResponseDto(servicoAlterado);
    }
}
