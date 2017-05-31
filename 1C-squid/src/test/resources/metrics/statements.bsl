function sayHello()

  ; // +1 empty statement

  a = 0; // +1 variable statement
  a = a + 1; // +1 expression statement

  if (false) then // +1 if statement
   // +0 compound statement
    raise new Exception(); // +1 throw statement
  endif

  ~label: // +0 labelled statement
  for i=1 to 10 do // +1 for statement
   // +0 compound statement
    break; // +1 break statement
  enddo

  while (false) do // +1 while statement
   // +0 compound statement
    continue; // +1 continue statement
  enddo


  for each a in b do // +1 for-in statement
   // +0 compound statement
  enddo

  try  // +1 try statement
  Исключение
  КонецПопытки


  return 1; // +1 return statement
КонецФункции
