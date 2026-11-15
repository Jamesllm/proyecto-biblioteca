/** Devuelve la fecha y hora actuales en el formato de un input datetime-local. */
export function ahoraLocal(): string {
  const ahora = new Date();
  const local = new Date(ahora.getTime() - ahora.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 16);
}
