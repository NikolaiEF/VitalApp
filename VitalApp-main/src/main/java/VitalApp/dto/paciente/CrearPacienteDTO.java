package VitalApp.dto.paciente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record CrearPacienteDTO(
        @NotBlank(message = "La cédula es obligatoria")
        @Pattern(regexp = "\\d+", message = "La cédula debe contener solo números")
        String id,

        @NotBlank(message = "El nombre es obligatorio")
        @Length(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        String nombre,

        @NotBlank @Length(max = 100)
        String apellido,
        
        @NotBlank @Length(max = 100)
        String email,

        @NotBlank @Length(max = 100)
        String telefono,

        @NotBlank @Length(max = 100)
        String rh,

        @NotBlank @Length(max = 100)
        String fechaNacimiento,

        @NotBlank @Length(max = 100)
        String direccion
) {}