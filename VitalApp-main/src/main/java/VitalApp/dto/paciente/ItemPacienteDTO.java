package VitalApp.dto.paciente;

public record ItemPacienteDTO(
        String id,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String rh,
        String fechaNacimiento,
        String direccion
) {}
