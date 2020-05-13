import java.lang.reflect.Array;

class NativeDictionary<T> {
    public int size;
    public String[] slots;
    public T[] values;

    public NativeDictionary(int sz, Class clazz) {
        size = sz;
        slots = new String[size];
        values = (T[]) Array.newInstance(clazz, this.size);
    }

    // всегда возвращает корректный индекс слота
    public int hashFun(String key) {
        byte[] arrBytesValue = key.getBytes();      // записываем байты элемента в массив
        int sum = 0;
        for (byte i :
                arrBytesValue) {
            sum += i;                               // считаем сумму байт
        }
        return sum % size;                          // вычисляем индекс
    }

    // возвращает true если ключ имеется,
    // иначе false
    public boolean isKey(String key) {
        int indexSlot = this.seekSlot(key);
        if (indexSlot == -1) return false;
        return slots[indexSlot] != null;
    }

    // гарантированно записываем
    // значение value по ключу key
    public void put(String key, T value) {
        int indexSlot = this.seekSlot(key);
        if (indexSlot == -1) return;
        slots[indexSlot] = key;
        values[indexSlot] = value;
    }

    // возвращает value для key,
    // или null если ключ не найден
    public T get(String key) {
        if (this.isKey(key)) {
            int indexSlot = this.seekSlot(key);
            return values[indexSlot];
        }
        return null;
    }

    // находит индекс пустого слота для значения, или -1
    public int seekSlot(String value) {
        int indexSlot = this.hashFun(value);                        // находим индекс элемента
        int countPasses = 0;                                        // вспомогательная переменная отвечающая за количество проходов по слотам
        while (slots[indexSlot] != null) {
            if (slots[indexSlot].equals(value))
                return indexSlot;                                   // если слот содержит такой же элемент то возвращать индекс этого слота
            indexSlot += 1;                                      // изменение индекса слота на шаг
            if (indexSlot >= size && countPasses < 1) {          // если индекс выходит за пределы таблицы и проходов меньше чем необходимо
                indexSlot = countPasses;                            // начинаем очередной проход сначала таблицы
                countPasses++;
            }
            if (indexSlot >= size && countPasses >= 1) {         // если вышли за пределы и прошли все слоты возвращаем -1
                return -1;
            }
        }
        return indexSlot;
    }
}