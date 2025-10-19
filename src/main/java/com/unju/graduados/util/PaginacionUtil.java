package com.unju.graduados.util;

import org.springframework.data.domain.Page;
import java.util.ArrayList;
import java.util.List;

public class PaginacionUtil {

    public static List<Integer> calcularRangoPaginas(Page<?> pagina, int pagesToShow) {

        int totalPages = pagina.getTotalPages();
        int currentPage = pagina.getNumber(); // Es el índice de la página actual (base 0)

        // 1. Calcula el inicio del rango (asegurando que no sea negativo)
        // Ejemplo: si pagesToShow es 5 y estás en la página 10, startPage será 8 (10 - 2)
        int startPage = Math.max(0, currentPage - (pagesToShow / 2));

        // 2. Calcula el fin del rango (asegurando que no exceda el total de páginas - 1)
        int endPage = Math.min(totalPages - 1, startPage + pagesToShow - 1);

        // 3. Si el rango final se acortó (porque estamos al final),
        // ajustamos el inicio para mantener el número de botones fijo
        if (endPage - startPage < pagesToShow - 1) {
            startPage = Math.max(0, endPage - pagesToShow + 1);
        }

        // 4. Crea y llena la lista de números de página
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }
}
