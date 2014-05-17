grammar Line;

tree_state
	:
		'hash' STRING
		add__table*
		add__link*
		add__column*
		add__row*
	;

add__table : 'table' table_name ;
add__link : 'link' table_name '=>'? column_name;
add__column : 'column' table_name ':'? column_name '='? nullable;
add__row : 'row' table_name '#'? uuid '{'? (column_name '->'? nullable)* '}'?;



uuid : STRING;
table_name : STRING;
column_name : STRING;

nullable : 'null' | STRING;

WS: [ \n\t]+ -> channel(HIDDEN);
STRING : '"' (~'"')+ '"';


/*

table "foo"
table "bar"
*/