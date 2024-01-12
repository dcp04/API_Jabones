package com.dwes.api.controladores;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwes.api.entidades.Categoria;
import com.dwes.api.servicios.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

	@Autowired
	private CategoriaService categoriaService;

	@GetMapping
	@Operation(summary = "Obtener todas las categorias", description = "Devuelve una lista paginada de categorias")
    @ApiResponse(responseCode = "200", description = "Lista de categorias obtenida exitosamente")
    @ApiResponse(responseCode = "204", description = "No hay categorias disponibles")
    @ApiResponse(responseCode = "400", description = "Parámetros de solicitud incorrectos")
	public ResponseEntity<Page<Categoria>> getAllCategorias(Pageable pageable) {
		Page<Categoria> categorias = categoriaService.findAll(pageable);
		return new ResponseEntity<>(categorias, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener una categoria por ID", description = "Devuelve una categoria específica por su ID")
    @ApiResponse(responseCode = "200", description = "Categoria encontrada",
        content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Categoria.class)) })
    @ApiResponse(responseCode = "404", description = "Categoria no encontrada")
	public ResponseEntity<Categoria> getCategoriaById(@PathVariable Long id) {
		Optional<Categoria> categoria = categoriaService.findById(id);
		return categoria.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping
	@Operation(summary = "Crear una nueva categoria", description = "Crea una nueva categoria y lo guarda en la base de datos")
    @ApiResponse(responseCode = "201", description = "Categoria creada con éxito")
    @ApiResponse(responseCode = "400", description = "Datos proporcionados para la nueva categoria son inválidos")
	public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
		Categoria nuevaCategoria = categoriaService.save(categoria);
		return new ResponseEntity<>(nuevaCategoria, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@Operation(summary = "Actualizar una categoria", description = "Actualiza los detalles de una categoria existente")
    @ApiResponse(responseCode = "200", description = "Categoria actualizada correctamente")
    @ApiResponse(responseCode = "404", description = "Categoria no encontrada para actualizar")
	public ResponseEntity<Categoria> updateCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
		if (categoriaService.existsById(id)) {
			Categoria categoriaActualizada = categoriaService.save(categoria);
			return new ResponseEntity<>(categoriaActualizada, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Actualizar parcialmente una categoría", description = "Actualiza parcialmente los detalles de una categoría")
	@ApiResponse(responseCode = "200", description = "Categoría actualizada parcialmente")
	@ApiResponse(responseCode = "404", description = "Categoría no encontrada para actualización parcial")
	public ResponseEntity<Categoria> actualizarParcialmenteCategoria(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		if (!categoriaService.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		Categoria categoriaActual = categoriaService.findById(id)
				.orElseThrow();

		// Aplica las actualizaciones parciales a los campos de la categoría
		if (updates.containsKey("nombre")) {
			categoriaActual.setNombre((String) updates.get("nombre"));
		}
		if (updates.containsKey("descripcion")) {
			categoriaActual.setDescripcion((String) updates.get("descripcion"));
		}

		// Guarda la categoría actualizada
		Categoria categoriaActualizada = categoriaService.save(categoriaActual);
		return ResponseEntity.ok(categoriaActualizada);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Borrar una categoria", description = "Elimina una categoria existente por su ID")
    @ApiResponse(responseCode = "204", description = "Categoria eliminada correctamente")
    @ApiResponse(responseCode = "404", description = "Categoria no encontrada para eliminar")
	public ResponseEntity<Void> deleteCategoria(@PathVariable Long id) {
		if (categoriaService.existsById(id)) {
			categoriaService.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}
