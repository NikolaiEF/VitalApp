package VitalApp.dto.citaMedica;

import VitalApp.dto.medico.ItemHorarioDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CrearCitaMedicaDTO(
        @NotBlank  String idPaciente, // ObjectId tiene 24 caracteres hex
        @NotBlank  String idMedico,
        @NotNull ItemHorarioDTO horario // DTO anidado para el horario
) {}