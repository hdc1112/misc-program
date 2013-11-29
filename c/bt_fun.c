#include <stdio.h>
int funa(void)
{
	printf("AAAAA");
	exit(-1);
	return 0;
}
int funb(int (*p)(void))
{
	char *h;
	h = &h;
 	/* point to the first variable */
			
	h += sizeof(char *);
	/* point to the old ebp */

	h += sizeof(char *);
	/* point to the return address */

  // p();
  
  typedef (int (*BBB)(void));
  
  BBB *abc = (int (**)(void))(h);

  *abc = p;

	 // (*(int (**)(void))(h)) = p;
	// (*(int (**)(void))) = p;
	//(   *(int (**)(void))(h)   );// = p;


   printf("abc\n");
	return 0;
}
int main()
{
	funb(funa);
	return 0;
}
