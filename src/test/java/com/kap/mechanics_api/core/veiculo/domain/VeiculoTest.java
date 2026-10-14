package com.kap.mechanics_api.core.veiculo.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class VeiculoTest {

    private static final LocalDateTime DATA = LocalDateTime.of(2026, 10, 7, 10, 30);

    private Veiculo civic() {
        return Veiculo.reconstruir(1, "ABC1D23", "Honda", "Civic", 2020, DATA);
    }

    @Test
    void novoDeveCriarVeiculoSemIdMantendoOsDados() {
        Veiculo veiculo = Veiculo.novo("ABC1D23", "Honda", "Civic", 2020, DATA);

        assertNull(veiculo.getId());
        assertEquals("ABC1D23", veiculo.getPlaca());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2020, veiculo.getAno());
        assertEquals(DATA, veiculo.getDataCriacao());
    }

    @Test
    void reconstruirDevePreservarId() {
        assertEquals(1, civic().getId());
    }

    @Test
    void deveRejeitarPlacaMarcaModeloEmBrancoOuNulos() {
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo(null, "Honda", "Civic", 2020, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo(" ", "Honda", "Civic", 2020, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", null, "Civic", 2020, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", "", "Civic", 2020, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", "Honda", null, 2020, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", "Honda", " ", 2020, DATA));
    }

    @Test
    void deveRejeitarAnoEDataDeCriacaoNulos() {
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", "Honda", "Civic", null, DATA));
        assertThrows(IllegalArgumentException.class, () -> Veiculo.novo("ABC1D23", "Honda", "Civic", 2020, null));
    }

    @Test
    void atualizarDeveAlterarSomenteOsCamposInformados() {
        Veiculo veiculo = civic();

        veiculo.atualizar(null, "Toyota", "  ", 2022);

        assertEquals("ABC1D23", veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2022, veiculo.getAno());
        assertEquals(DATA, veiculo.getDataCriacao());
    }

    @Test
    void atualizarDevePermitirTrocarPlacaEModelo() {
        Veiculo veiculo = civic();

        veiculo.atualizar("XYZ9K88", null, "Corolla", null);

        assertEquals("XYZ9K88", veiculo.getPlaca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals("Honda", veiculo.getMarca());
        assertEquals(2020, veiculo.getAno());
    }
}
