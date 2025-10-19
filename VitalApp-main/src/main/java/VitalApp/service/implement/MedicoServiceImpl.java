package VitalApp.service.implement;

import VitalApp.dto.medico.*;
import VitalApp.model.documents.Medico;
import VitalApp.model.vo.HorarioMedico;
import VitalApp.repository.MedicoRepository;
import VitalApp.repository.PacienteRepository;
import VitalApp.service.service.MedicoService;
import VitalApp.service.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicoServiceImpl implements MedicoService {

    private final MedicoRepository medicoRepo;
    private final EmailService emailService;

    @Override
    public String crearMedico(CrearMedicoDTO medicoDTO) throws Exception {

       if (medicoRepo.existsById(medicoDTO.id().trim())) {
           throw new Exception("Ya existe un medico con la cédula: " + medicoDTO.id());
       }

        if (medicoDTO.nombre() == null || medicoDTO.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        Medico nuevoMedico = Medico.builder()
                .id(medicoDTO.id().trim())
                .nombre(medicoDTO.nombre().trim())
                .apellido(medicoDTO.apellido().trim())
                .especialidad(medicoDTO.especialidad().trim())
                .correo(medicoDTO.correo().trim())
                .horariosDisponibles(new ArrayList<>())
                .jornadaMedico(medicoDTO.jornadaMedico())
                .build();

        String id = medicoRepo.save(nuevoMedico).getId();

        emailService.enviarBienvenida(
                medicoDTO.correo(),
                medicoDTO.nombre(),
                true // es médico
        );

        return id;
    }

    @Override
    public String editarMedico(EditarMedicoDTO medicoDTO) throws Exception {
        Medico medico = obtenerMedicoPorId(medicoDTO.id());

        medico.setNombre(medicoDTO.nombre());
        if (medicoDTO.nombre() == null || medicoDTO.nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        medico.setEspecialidad(medicoDTO.especialidad());
        medico.setCorreo(medicoDTO.email());

        return medicoRepo.save(medico).getId();
    }

    @Override
    public String eliminarMedico(String id) throws Exception {
        Medico medico = obtenerMedicoPorId(id);
        medicoRepo.delete(medico);
        return id;
    }

    @Override
    public InformacionMedicoDTO obtenerInformacionMedico(String id) throws Exception {
        Medico medico = obtenerMedicoPorId(id);
        return new InformacionMedicoDTO(
                medico.getId(),
                medico.getNombre(),
                medico.getEspecialidad()
        );
    }

    @Override
    public List<ItemMedicoDTO> listarMedicos() {
        return medicoRepo.findAll()
                .stream()
                .map(m -> new ItemMedicoDTO(m.getId(), m.getNombre(), m.getApellido(), m.getEspecialidad(), m.getCorreo(), m.getJornadaMedico()))
                .collect(Collectors.toList());
    }

    @Override
    public String agregarHorario(String idMedico, CrearHorarioDTO horarioDTO) throws Exception {
        Medico medico = obtenerMedicoPorId(idMedico);

        // Validar rango horario
        if (!horarioDTO.horaInicio().isBefore(horarioDTO.horaFin())) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora fin");
        }

        HorarioMedico nuevoHorario = HorarioMedico.builder()
                .fecha(horarioDTO.fecha())
                .horaInicio(horarioDTO.horaInicio())
                .horaFin(horarioDTO.horaFin())
                .reservado(false)
                .build();

        // Validar solapamiento
        boolean horarioSolapado = medico.getHorariosDisponibles().stream()
                .anyMatch(h -> h.getFecha().equals(nuevoHorario.getFecha()) &&
                        !(h.getHoraFin().isBefore(nuevoHorario.getHoraInicio()) ||
                                h.getHoraInicio().isAfter(nuevoHorario.getHoraFin())));

        if (horarioSolapado) {
            throw new IllegalStateException("El nuevo horario se solapa con uno existente");
        }

        medico.getHorariosDisponibles().add(nuevoHorario);
        return medicoRepo.save(medico).getId();
    }

    @Override
    public List<ItemHorarioDTO> listarHorarios(String idMedico) throws Exception {
        return obtenerMedicoPorId(idMedico).getHorariosDisponibles()
                .stream()
                .map(this::convertToItemHorarioDTO)
                .collect(Collectors.toList());
    }

    private ItemHorarioDTO convertToItemHorarioDTO(HorarioMedico horario) {
        return new ItemHorarioDTO(
                horario.getFecha(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.isReservado()
        );
    }

    private boolean existeNombre(String nombre) {
        return medicoRepo.existsByNombre(nombre);
    }

    private Medico obtenerMedicoPorId(String id) throws Exception {
        return medicoRepo.findById(id)
                .orElseThrow(() -> new Exception("Médico no encontrado con ID: " + id));
    }
}