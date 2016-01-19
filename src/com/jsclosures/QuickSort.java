package com.jsclosures;

import java.util.ArrayList;


public class QuickSort extends DataBean
{
     public QuickSort()
     {
          super();
     }

     public static void sortAscending(ArrayList<DataBean> a)
     {
          sort(a,0,a.size() - 1);
     }

     public static void sortAscending(ArrayList<DataBean> a,String column)
     {
          sortByColumn(a,0,a.size() - 1,column);
     }

     public static void sortDescending(ArrayList<DataBean> a)
     {
          sort(a,0,a.size() - 1);

          for(int i = 0,j = a.size() - 1;i < a.size();i++,j--)
          {
               if(i >= j)
               {
                    break;
               }
               swap(a,i,j);
          }
     }

     public static void sortDescending(ArrayList<DataBean> a,String column)
     {
          sortByColumn(a,0,a.size() - 1,column);

          for(int i = 0,j = a.size() - 1;i < a.size();i++,j--)
          {
               if(i >= j)
               {
                    break;
               }
               swap(a,i,j);
          }
     }

     private static void sortByColumn(ArrayList<DataBean> a,int lo0,int hi0,String column)
     {
          int lo = lo0;
          int hi = hi0;
          String mid;

          if(hi0 > lo0)
          {

               /* Arbitrarily establishing partition element as the midpoint of
                * the array.
                */
               mid = ((DataBean)a.get((lo0 + hi0) / 2)).getString(column);

               // loop through the array until indices cross
               while(lo <= hi)
               {
                    /* find the first element that is greater than or equal to
                     * the partition element starting from the left Index.
                     */
                    while(lo < hi0 && a.get(lo).getString(column).compareToIgnoreCase(mid) < 0)
                    {
                         ++lo;
                    }

                    /* find an element that is smaller than or equal to
                     * the partition element starting from the right Index.
                     */
                    while(hi > lo0 && a.get(hi).getString(column).compareToIgnoreCase(mid) > 0)
                    {
                         --hi;
                    }

                    // if the indexes have not crossed, swap
                    if(lo <= hi)
                    {
                         swap(a,lo,hi);
                         ++lo;
                         --hi;
                    }
               }

               /* If the right index has not reached the left side of array
                * must now sort the left partition.
                */
               if(lo0 < hi)
               {
                    sortByColumn(a,lo0,hi,column);

                    /* If the left index has not reached the right side of array
                     * must now sort the right partition.
                     */
               }
               if(lo < hi0)
               {
                    sortByColumn(a,lo,hi0,column);

               }
          }
     }

     private static void sort(ArrayList<DataBean> a,int lo0,int hi0)
     {
          int lo = lo0;
          int hi = hi0;
          String mid;

          if(hi0 > lo0)
          {

               /* Arbitrarily establishing partition element as the midpoint of
                * the array.
                */
               mid = a.get((lo0 + hi0) / 2).toString();

               // loop through the array until indices cross
               while(lo <= hi)
               {
                    /* find the first element that is greater than or equal to
                     * the partition element starting from the left Index.
                     */
                    while(lo < hi0 && a.get(lo).toString().compareToIgnoreCase(mid) < 0)
                    {
                         ++lo;
                    }

                    /* find an element that is smaller than or equal to
                     * the partition element starting from the right Index.
                     */
                    while(hi > lo0 && a.get(hi).toString().compareToIgnoreCase(mid) > 0)
                    {
                         --hi;
                    }

                    // if the indexes have not crossed, swap
                    if(lo <= hi)
                    {
                         swap(a,lo,hi);
                         ++lo;
                         --hi;
                    }
               }

               /* If the right index has not reached the left side of array
                * must now sort the left partition.
                */
               if(lo0 < hi)
               {
                    sort(a,lo0,hi);

                    /* If the left index has not reached the right side of array
                     * must now sort the right partition.
                     */
               }
               if(lo < hi0)
               {
                    sort(a,lo,hi0);

               }
          }
     }

     private static void swap(ArrayList<DataBean> a,int i,int j)
     {
          DataBean T = a.get(i);
          a.add(i,a.get(j));
          a.add(j,T);
     }
}
/*end*/
