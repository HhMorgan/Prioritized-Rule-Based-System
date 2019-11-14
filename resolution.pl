:-use_module(library(chr)).
:-chr_constraint a/0,a/1,b/0,b/1,c/0,c/1,d/0,d/1,e/0,e/1,start/0,conflictdone/0,fire/0,id/1,history/1,match/2,match/3.

id(I), a <=> a(I), I1 is I + 1, id(I1).
id(I), b <=> b(I), I1 is I + 1, id(I1).
id(I), c <=> c(I), I1 is I + 1, id(I1).
id(I), d <=> d(I), I1 is I + 1, id(I1).
id(I), e <=> e(I), I1 is I + 1, id(I1).

start, a(ID1), b(ID2), c(ID3) ==> match(r1,4,[ID1,ID2,ID3]).
start, a(ID1), b(ID2), d(ID3) ==> match(r2,7,[ID1,ID2,ID3]).
start, e(ID1) ==> match(r3,4,[ID1]).

start <=> conflictdone.
history(L),conflictdone\match(R,_,IDs) <=> member((R,FIDs),L), sort(IDs,II), sort(FIDs,II) | true.

conflictdone,match(_,O1,_)\ match(_,O2,_) <=> O1>O2 | true.
conflictdone,match(_,O1,_)\ match(_,O2,_) <=> O1=O2, R is random(2), R = 0 | true.

conflictdone <=> fire.

a(ID1),b(ID2)\c(ID3),history(L),fire,match(r1,_,[ID1,ID2,ID3]) <=> print('fired r1'),nl,c,d,history([(r1,[ID1,ID2,ID3])|L]),start.
a(ID1),b(ID2),d(ID3),history(L),fire,match(r2,_,[ID1,ID2,ID3]) <=> print('fired r2'),nl,c,e,history([(r2,[ID1,ID2,ID3])|L]),start.
e(ID1)\history(L),fire,match(r3,_,[ID1]) <=> print('fired r3'),nl,c,d,history([(r3,[ID1])|L]),start.
