package VitalApp.service.implement;

import VitalApp.dto.citaMedica.*;
import VitalApp.dto.medico.ItemHorarioDTO;
import VitalApp.model.documents.CitaMedica;
import VitalApp.model.documents.Medico;
import VitalApp.model.documents.Paciente;
import VitalApp.model.enums.EstadoCita;
import VitalApp.model.vo.HorarioMedico;
import VitalApp.model.vo.ResultadoMedico;
import VitalApp.repository.CitaMedicaRepository;
import VitalApp.repository.MedicoRepository;
import VitalApp.repository.PacienteRepository;
import VitalApp.service.service.CitaMedicaService;
import VitalApp.service.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CitaMedicaServiceImpl implements CitaMedicaService {

    private final CitaMedicaRepository citaRepo;
    private final MedicoRepository medicoRepo;
    private final PacienteRepository pacienteRepo;
    private final EmailService emailService;

    @Override
    public String agendarCita(CrearCitaMedicaDTO dto) throws Exception {
        // 1️⃣ Obtener médico y paciente
        Medico medico = obtenerMedicoPorId(dto.idMedico());
        Paciente paciente = obtenerPacientePorId(dto.idPaciente());
        ItemHorarioDTO horarioDTO = dto.horario();

        // 2️⃣ Buscar el bloque de horario base (sin fecha)
        HorarioMedico horarioBase = medico.getHorariosDisponibles().stream()
                .filter(h -> h.getHoraInicio().equals(horarioDTO.horaInicio())
                        && h.getHoraFin().equals(horarioDTO.horaFin()))
                .findFirst()
                .orElseThrow(() -> new Exception("Horario no disponible para el médico seleccionado."));

        if (horarioBase.isReservado()) {
            throw new IllegalStateException("El horario ya está reservado.");
        }

        // 3️⃣ Marcarlo como reservado (si quieres manejar reservas genéricas)
        horarioBase.setReservado(true);
        medicoRepo.save(medico);

        // 4️⃣ Crear una copia del horario con la fecha de la cita
        HorarioMedico horarioConFecha = HorarioMedico.builder()
                .horaInicio(horarioDTO.horaInicio())
                .horaFin(horarioDTO.horaFin())
                .reservado(true)
                .build();

        // 5️⃣ Crear la cita médica con la fecha concreta
        CitaMedica cita = CitaMedica.builder()
                .idCliente(new ObjectId(paciente.getId()))
                .idMedico(new ObjectId(medico.getId()))
                .horario(horarioConFecha)
                .estado(EstadoCita.PENDIENTE)
                .build();

        String id = citaRepo.save(cita).getId();

        // 6️⃣ Enviar confirmaciones (usando la fecha del DTO, no del horario base)
        emailService.enviarConfirmacionCita(
                paciente.getEmail(),
                paciente.getNombre(),
                horarioDTO.fecha().toString(),
                horarioDTO.horaInicio().toString()
        );

        emailService.enviarConfirmacionCita(
                medico.getCorreo(),
                medico.getNombre(),
                horarioDTO.fecha().toString(),
                horarioDTO.horaInicio().toString()
        );

        return id;
    }


//    @Override
//    public String cancelarCita(String idCita) throws Exception {
//        CitaMedica cita = obtenerCitaPorId(idCita);
//
//        if (cita.getEstado() == EstadoCita.CANCELADA) {
//            throw new IllegalStateException("La cita ya fue cancelada.");
//        }
//
//        cita.setEstado(EstadoCita.CANCELADA);
//        liberarHorarioCita(cita);
//
//        return citaRepo.save(cita).getId();
//    }

    @Override
    public List<ItemCitaMedicaDTO> listarCitasPorPaciente(String idPaciente) {
        ObjectId pacienteId = new ObjectId(idPaciente);
        return citaRepo.findByIdCliente(pacienteId).stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemCitaMedicaDTO> listarCitasPorMedico(String idMedico) {
        ObjectId medicoId = new ObjectId(idMedico);
        return citaRepo.findByIdMedico(medicoId).stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String agregarResultadoMedico(String idCita, CrearResultadoMedicoDTO dto) throws Exception {
        CitaMedica cita = obtenerCitaPorId(idCita);

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden agregar resultados a citas en estado PENDIENTE.");
        }

        ResultadoMedico resultado = ResultadoMedico.builder()
                .idCitaMedica(cita.getId())
                .descripcion(dto.descripcion())
                .diagnostico(dto.diagnostico())
                .recomendaciones(dto.recomendaciones())
                .fechaRegistro(LocalDateTime.now())
                .build();

        cita.setResultado(resultado);
        cita.setEstado(EstadoCita.VISTA);

        return citaRepo.save(cita).getId();
    }

    @Override
    public InformacionResultadoMedicoDTO obtenerResultadoMedico(String idCita) throws Exception {
        CitaMedica cita = obtenerCitaPorId(idCita);

        ResultadoMedico resultado = cita.getResultado();
        if (resultado == null) {
            throw new Exception("La cita no tiene resultado médico registrado.");
        }

        return new InformacionResultadoMedicoDTO(
                cita.getId(),
                resultado.getDescripcion(),
                resultado.getDiagnostico(),
                resultado.getRecomendaciones(),
                resultado.getFechaRegistro()
        );
    }

    // Métodos auxiliares

    private CitaMedica obtenerCitaPorId(String id) throws Exception {
        return citaRepo.findById(id)
                .orElseThrow(() -> new Exception("Cita médica no encontrada con ID: " + id));
    }

    private Medico obtenerMedicoPorId(String id) throws Exception {
        return medicoRepo.findById(id)
                .orElseThrow(() -> new Exception("Médico no encontrado con ID: " + id));
    }

    private Paciente obtenerPacientePorId(String id) throws Exception {
        return pacienteRepo.findById(id)
                .orElseThrow(() -> new Exception("Paciente no encontrado con ID: " + id));
    }

//    private void liberarHorarioCita(CitaMedica cita) throws Exception {
//        Medico medico = obtenerMedicoPorId(cita.getIdMedico().toHexString());
//
//        Optional<HorarioMedico> horario = medico.getHorariosDisponibles().stream()
//                .filter(h -> h.getFecha().equals(cita.getHorario().getFecha())
//                        && h.getHoraInicio().equals(cita.getHorario().getHoraInicio())
//                        && h.getHoraFin().equals(cita.getHorario().getHoraFin()))
//                .findFirst();
//
//        horario.ifPresent(h -> h.setReservado(false));
//        medicoRepo.save(medico);
//    }

    private ItemCitaMedicaDTO convertToItemDTO(CitaMedica cita) {
        return new ItemCitaMedicaDTO(
                cita.getId(),
                cita.getIdCliente(),
                cita.getIdMedico(),
                cita.getHorario(),
                cita.getEstado(),
                cita.getResultado()
        );
    }
}