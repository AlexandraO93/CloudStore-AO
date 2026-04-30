package se.jensen.alexandra.fakestoreuserservice.dto;

public record ProductDTO(
        Long id,
        String title,
        Double price,
        String description,
        String category,
        String image
) {
}
