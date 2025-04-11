lexer grammar Directives;

BYTE_SIZE
    : [0-9]+ ('.' [0-9]+)? [KkMmGgTt]? [Bb]
    ;

TIME_DURATION
    : [0-9]+ ('.' [0-9]+)? [MmHhSs] [Ss]?
    ;

