function example() // +1 functionDeclaration

  if (foo) // +1 ifStatement
  then
    return 0; // +1 returnStatement
  endif;

  for i = 0 to 10 do // +1 iterationStatement

  enddo;

  while false do // +1 iterationStatement

  enddo;


  Попытка
    ВызватьИсключение "err"; // +1 throw
  Исключение
  КонецПопытки;

  return 1; // +0 last returnStatement
КонецФункции
