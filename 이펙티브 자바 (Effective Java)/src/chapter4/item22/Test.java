package chapter4.item22;

import static chapter4.item22.PhysicalConstantsClass.AVOGADRO_NUMBER;

class Test {
    double atoms(double mols) {
        return AVOGADRO_NUMBER * mols;
    }
    // ... PhysicalConstants를 빈번히 사용한다면 정적 임포트가 값어치를 한다.
}
